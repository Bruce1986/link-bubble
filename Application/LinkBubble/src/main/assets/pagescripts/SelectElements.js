/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

(function () {
  if (window.LinkBubble.selectOption) { return; }
  window.LinkBubble.lastSelectFocused = null;
  window.LinkBubble.selectOption = function (index) {
    const select = window.LinkBubble.lastSelectFocused;
    select.selectedIndex = index;
    select.previousElementSibling.textContent = select[index].text;
  };
  const positioningProps = ['float', 'position', 'width', 'height', 'left', 'top', 'margin-left', 'margin-top', 'padding-left', 'padding-top', 'border', 'background'];
  const els = document.getElementsByTagName('select');
  function maskSelects () {
    /* Remove all previous select masks if the next element is not a select any longer. */
    Array.prototype.forEach.call(document.querySelectorAll('.__link_bubble__select_mask__'), function (mask) {
      if (mask.nextElementSibling && mask.nextElementSibling.nodeName.toLowerCase() === 'select') { return; }
      mask.parentNode.removeChild(mask);
    });

    Array.prototype.forEach.call(els, function (select) {
      let mask = select.previousElementSibling;
      /* Insert and style for new selects */
      if (!mask || mask.className !== '__link_bubble__select_mask__') {
        mask = document.createElement('div');
        mask.className = '__link_bubble__select_mask__';
        mask.style.webkitAppearance = 'menulist';
        const computedStyle = window.getComputedStyle(select);

        for (const prop of positioningProps) {
          mask.style[prop] = computedStyle.getPropertyValue(prop);
        }
        select.parentNode.insertBefore(mask, select);
        select.style.display = 'none';

        mask.addEventListener('click', function (e) {
          e.preventDefault();
          window.LinkBubble.lastSelectFocused = select;
          const keyAndValues = [select.selectedIndex];
          for (const option of select) {
            keyAndValues.push(option.text);
            keyAndValues.push(option.value);
          }
          window.LinkBubble.onSelectElementInteract(JSON.stringify(keyAndValues));
        });
      }
      mask.textContent = select[select.selectedIndex].text;
    });
  }
  /* Mask all selects when the script is injected. */
  maskSelects();
  /* Use a mutation observer for dynamic selects added after page load. */
  const MutationObserver = window.MutationObserver || window.WebKitMutationObserver;
  const observer = new MutationObserver(function (mutations) {
    mutations.forEach(function (mutation) {
      let changed = false;
      const allChangedNodes = [...mutation.addedNodes, ...mutation.removedNodes];
      allChangedNodes.forEach(function (changedNode) {
        if ((changedNode.querySelector && changedNode.querySelector('select')) || changedNode.nodeName.toLowerCase() === 'select') {
          changed = true;
        }
      });
      if (changed) {
        maskSelects();
      }
    });
  });
  const config = { attributes: false, childList: true, characterData: false, subtree: true };
  observer.observe(document, config);
})();

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

/* global adInfoObject */
(function () {
  const adHost = window.location.protocol + '//cdn.brave.com';
  const fallbackNodeDataForCommon = {};
  for (let i = 0; i < adInfoObject.length; i++) {
    const selector = '[id="' + adInfoObject[i].rid + '"]';
    const node = document.querySelector(selector);
    if (!node) {
      continue;
    }

    /* Skip over known common elements */
    if (adInfoObject[i].rid.startsWith('google_ads_iframe_') ||
        adInfoObject[i].rid.endsWith('__container__')) {
      fallbackNodeDataForCommon[node.id] = adInfoObject[i];

      continue;
    }

    processAdNode(node, adInfoObject[i]);

    /* Common selectors which could be on every page */
    const commonSelectors = [
      '[id^="google_ads_iframe_"][id$="__container__"]',
      '[id^="ad-slot-banner-"]',
      '[data-ad-slot]'
    ];
    commonSelectors.forEach(commonSelector => {
      const nodes = document.querySelectorAll(commonSelector);
      if (!nodes) {
        return;
      }
      Array.from(nodes).forEach(node => {
        processAdNode(node, fallbackNodeDataForCommon[node.id]);
      });
    });
  }

  function processAdNode (node, iframeData) {
    const adSize = getAdSize(node, iframeData);
    /* Could not determine the ad size, so just skip this replacement */
    if (!adSize) {
      /* we have a replace node node but no replacement, so just display none on it */
      node.style.display = 'none';
      return;
    }

    /* generate a random segment */
    /* todo - replace with renko targeting */
    const segments = ['IAB2', 'IAB17', 'IAB14', 'IAB21', 'IAB20'];
const segment = segments[Math.floor(Math.random() * segments.length)];
    const time_in_segment = new Date().getSeconds();
    const segment_expiration_time = 0; /* no expiration */

    /* ref param for referrer when possible */
const srcUrl = `${adHost}?width=${adSize[0]}&height=${adSize[1]}&seg=${segment}:${time_in_segment}:${segment_expiration_time}`;
const src = `<html><body style="width: ${adSize[0]}px; height: ${adSize[1]}px; padding: 0; margin: 0; overflow: hidden;"><script src="${srcUrl}"></script></body></html>`;

    if (node.tagName === 'IFRAME') {
      node.srcdoc = src;
      node.sandbox = 'allow-scripts allow-popups';
    } else {
      while (node.firstChild) {
        node.removeChild(node.firstChild);
      }
      const iframe = document.createElement('iframe');
      iframe.style.padding = 0;
      iframe.style.border = 0;
      iframe.style.margin = 0;
      iframe.style.width = adSize[0] + 'px';
      iframe.style.height = adSize[1] + 'px';
      iframe.srcdoc = src;
      iframe.sandbox = 'allow-scripts allow-popups';
      node.appendChild(iframe);
      ensureNodeVisible(node);
      if (node.parentNode) {
        ensureNodeVisible(node.parentNode);
        if (node.parentNode) {
          ensureNodeVisible(node.parentNode.parentNode);
        }
      }
    }
  }

  function getAdSize (node, iframeData) {
    const acceptableAdSizes = [
      [970, 250],
      [970, 90],
      [728, 90],
      [300, 250],
      [300, 600],
      [160, 600],
      [120, 600],
      [320, 50]
    ];
for (const adSize of acceptableAdSizes) {
      if (node.offsetWidth === adSize[0] && node.offsetHeight >= adSize[1] ||
          node.offsetWidth >= adSize[0] && node.offsetHeight === adSize[1]) {
        return adSize;
      }
    }

    if (iframeData) {
      return [iframeData.width || iframeData.w, iframeData.height || iframeData.h];
    }

    return null;
  }

  function ensureNodeVisible (node) {
    if (document.defaultView.getComputedStyle(node).display === 'none') {
      node.style.display = '';
    }
    if (document.defaultView.getComputedStyle(node).zIndex === '-1') {
      node.style.zIndex = '';
    }
  }
})();

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

(function () {
  const links = document.head.getElementsByTagName('link');
  const linksArray = [];
  for (const l of links) {
    if (l.rel && l.rel.indexOf('apple-touch-icon') !== -1) {
      const s = '@@@' + l.rel + ',' + l.href + ',' + l.sizes + '###';
      linksArray.push(s);
    }
  }
  if (linksArray.length > 0) {
    window.LinkBubble.onTouchIconLinks(linksArray.toString());
  }
})();

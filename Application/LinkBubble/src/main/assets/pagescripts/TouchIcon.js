/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

(function () {
  const links = document.head.getElementsByTagName('link');
  let linksArray = null;
  let linksCount = 0;
  for (const link in links) {
    if (links.hasOwnProperty(link)) {
      const l = links[link];
      if (l.rel != null && l.rel.indexOf('apple-touch-icon') !== -1) {
        if (linksArray == null) {
          linksArray = [];
        }
        const s = '@@@' + l.rel + ',' + l.href + ',' + l.sizes + '###';
        linksArray[linksCount] = s;
        linksCount++;
      }
    }
  }
  if (linksCount > 0) {
    window.LinkBubble.onTouchIconLinks(linksArray.toString());
  }
})();

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

(function () {
  function detectYoutubeEmbeds () {
    const elems = document.getElementsByTagName('*');
    let i;
    const YOUTUBE_EMBED_PREFIX = '//www.youtube.com/embed/';
    let resultArray = null;
    let resultCount = 0;
    for (i in elems) {
      const elem = elems[i];
      if (elem.src != null && elem.src.indexOf(YOUTUBE_EMBED_PREFIX) !== -1) {
        if (resultArray == null) {
          resultArray = [];
        }
        resultArray[resultCount] = elem.src;
        resultCount++;
      }
    }
    if (resultCount > 0) {
      window.LinkBubble.onYouTubeEmbeds(resultArray.toString());
    }
  }

  detectYoutubeEmbeds();
})();

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

(function () {
  function detectYoutubeEmbeds () {
const elems = document.querySelectorAll('iframe, embed, object');
    const YOUTUBE_EMBED_PREFIX = '//www.youtube.com/embed/';
    const resultArray = [];
    for (const elem of elems) {
      const src = elem.src || elem.data;
      if (src && src.indexOf(YOUTUBE_EMBED_PREFIX) !== -1) {
        resultArray.push(src);
      }
    }
    if (resultArray.length > 0) {
      window.LinkBubble.onYouTubeEmbeds(resultArray.toString());
    }
  }

  detectYoutubeEmbeds();
})();

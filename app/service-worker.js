var cacheName = 'beacheesPWA-1-0';
var dataCacheName = 'beacheesData-1-0';

//TODO list file to cache
var filesToCache = ['/',
   '/index.html',
];

// INFO install service worker
self.addEventListener('install', function (e) {
   console.log('[ServiceWorker] Install');
   e.waitUntil(
      caches.open(cacheName).then(function (cache) {
         console.log('[ServiceWorker] Caching app shell');
         return cache.addAll(filesToCache);
      })
   );
});

// INFO update cache whenever any app shell files change
self.addEventListener('activate', function (e) {
   console.log('[ServiceWorker] Activate');
   e.waitUntil(
      caches.keys().then(function (keyList) {
         return Promise.all(keyList.map(function (key) {
            if (key !== cacheName && key !== dataCacheName) {
               console.log('[ServiceWorker] Removing old cache', key);
               return caches.delete(key);
            }
         }));
      })
   );
   return self.clients.claim();
});

// INFO fetch requests made by client
self.addEventListener('fetch', function (e) {
   console.log('[ServiceWorker] Fetch', e.request.url);
   // Serve data from cache
   //TODO edit url and tipe of cache
   var dataUrl = 'https://query.yahooapis.com/v1/public/yql';
   if (e.request.url.indexOf(dataUrl) > -1) {
      /*
       * When the request URL contains dataUrl, the app is asking for fresh
       * weather data. In this case, the service worker always goes to the
       * network and then caches the response. This is called the "Cache then
       * network" strategy:
       * https://jakearchibald.com/2014/offline-cookbook/#cache-then-network
       */
      e.respondWith(
         caches.open(dataCacheName).then(function (cache) {
            return fetch(e.request).then(function (response) {
               cache.put(e.request.url, response.clone());
               return response;
            });
         })
      );
   } else {
      /*
       * The app is asking for app shell files. In this scenario the app uses the
       * "Cache, falling back to the network" offline strategy:
       * https://jakearchibald.com/2014/offline-cookbook/#cache-falling-back-to-network
       */
      e.respondWith(
         caches.match(e.request).then(function (response) {
            return response || fetch(e.request);
         })
      );
   }
});
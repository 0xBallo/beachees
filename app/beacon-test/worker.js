const cacheName = 'beacheesPWA-1-0';
const dataCacheName = 'beacheesData-1-0';


//TODO update list file to cache
const filesToCache = ['/',
   '/index.html',
];

// INFO install service worker
self.addEventListener('install', (e) => {
   console.log('[ServiceWorker] Install');
   e.waitUntil(
      caches.open(cacheName).then((cache) => {
         console.log('[ServiceWorker] Caching app shell');
         return cache.addAll(filesToCache);
      })
   );
});

//========================= OFFLINE MODE ===========================================

// INFO update cache whenever any app shell files change
self.addEventListener('activate', (e) => {
   console.log('[ServiceWorker] Activate');
   e.waitUntil(
      caches.keys().then((keyList) => {
         return Promise.all(keyList.map((key) => {
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
self.addEventListener('fetch', (e) => {
   console.log('[ServiceWorker] Fetch', e.request.url);
   // Serve data from cache
   //TODO edit url and tipe of cache
   let dataUrl = 'https://query.yahooapis.com/v1/public/yql';
   if (e.request.url.indexOf(dataUrl) > -1) {
      /*
       * When the request URL contains dataUrl, the app is asking for fresh
       * weather data. In this case, the service worker always goes to the
       * network and then caches the response. This is called the "Cache then
       * network" strategy:
       * https://jakearchibald.com/2014/offline-cookbook/#cache-then-network
       */
      e.respondWith(
         caches.open(dataCacheName).then((cache) => {
            return fetch(e.request).then((response) => {
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
         caches.match(e.request).then((response) => {
            return response || fetch(e.request);
         })
      );
   }
});

//========================= PUSH NOTIFICATION ======================================

self.addEventListener('push', (event) => {
   console.log('[Service Worker] Push Received.');
   console.log(`[Service Worker] Push had this data: "${event.data.text()}"`);

   const title = 'Beachees Monitoring Systems';
   const options = {
      body: event.data.json().message,
      icon: 'images/icon.png',
      badge: 'images/badge.png'
   };

   const notificationPromise = self.registration.showNotification(title, options);
   event.waitUntil(notificationPromise);
});
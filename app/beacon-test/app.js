// Hard-coded, replace with your public key

(() => {
   'use strict';

   let app = {
      publicVapidKey: 'BNkCYDAYelxetRFIQrxVeMPaEZIebQxQf7MbEXT2ioP9bUnpGU-0wKq0Cq4J8leEXyVZMhrHu57GxafDeiSXTF4',
      registration: {},
      subscription: {},
      urlBase64ToUint8Array: (base64String) => {
         const padding = '='.repeat((4 - base64String.length % 4) % 4);
         const base64 = (base64String + padding)
            .replace(/\-/g, '+')
            .replace(/_/g, '/');

         const rawData = window.atob(base64);
         const outputArray = new Uint8Array(rawData.length);

         for (let i = 0; i < rawData.length; ++i) {
            outputArray[i] = rawData.charCodeAt(i);
         }
         return outputArray;
      }
   }

   // Service worker and Push Notifications
   app.run = async () => {
      console.log('Registering service worker');
      app.registration = await navigator.serviceWorker.
      register('/worker.js', {
         scope: '/'
      });
      console.log('Registered service worker');

      console.log('Registering push service');
      app.subscription = await app.registration.pushManager.
      subscribe({
         userVisibleOnly: true,
         // The `urlBase64ToUint8Array()` function is the same as in
         // https://www.npmjs.com/package/web-push#using-vapid-key-for-applicationserverkey
         applicationServerKey: app.urlBase64ToUint8Array(app.publicVapidKey)
      });
      console.log('Registered push service');

      console.log('Sending subscription request');
      // TODO: set user based on signin trough QR code
      const payload = {
         subscription: app.subscription,
         user: 'PM12'
      };
      await fetch('http://localhost:3001/api/subscribe', {
         method: 'POST',
         body: JSON.stringify(payload),
         headers: {
            'content-type': 'application/json'
         }
      });
      console.log('Subscribed to beaches service');
   }

   //Service Worker registration
   if ('serviceWorker' in navigator) {
      console.log('Registering service worker');

      app.run().catch(error => console.error(error));
   }

})();
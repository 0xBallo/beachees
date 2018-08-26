// Hard-coded, replace with your public key
const publicVapidKey = 'BNkCYDAYelxetRFIQrxVeMPaEZIebQxQf7MbEXT2ioP9bUnpGU-0wKq0Cq4J8leEXyVZMhrHu57GxafDeiSXTF4';

if ('serviceWorker' in navigator) {
   console.log('Registering service worker');

   run().catch(error => console.error(error));
}

// Service worker and Push Notifications
async function run() {
   console.log('Registering service worker');
   const registration = await navigator.serviceWorker.
   register('/worker.js', {
      scope: '/'
   });
   console.log('Registered service worker');

   console.log('Registering push service');
   const subscription = await registration.pushManager.
   subscribe({
      userVisibleOnly: true,
      // The `urlBase64ToUint8Array()` function is the same as in
      // https://www.npmjs.com/package/web-push#using-vapid-key-for-applicationserverkey
      applicationServerKey: urlBase64ToUint8Array(publicVapidKey)
   });
   console.log('Registered push service');

   console.log('Sending subscription request');
   // TODO: set user based on registrazione trough QR code
   const payload = {
      subscription: subscription,
      user: 'PM12'
   };
   await fetch('/subscribe', {
      method: 'POST',
      body: JSON.stringify(payload),
      headers: {
         'content-type': 'application/json'
      }
   });
   console.log('Subscribed to beaches service');
}
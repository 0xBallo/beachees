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

      //TODO: Test bluetooth scanning 1
      //   navigator.bluetooth.requestLEScan({
      //       filters: [{
      //           manufacturerData: {
      //               0x004C: {
      //                   dataPrefix: new Uint8Array([
      //                       0x02, 0x15, // iBeacon identifier.
      //                       0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15  // My beacon UUID.
      //                   ])
      //               }
      //           }
      //       }],
      //       options: {
      //           keepRepeatedDevices: true,
      //       }
      //   }).then((data) => {
      //       navigator.bluetooth.addEventListener('advertisementreceived', event => {
      //           let appleData = event.manufacturerData.get(0x004C);
      //           if (appleData.byteLength != 23) {
      //               // Isn’t an iBeacon.
      //               return;
      //           }
      //           let major = appleData.getUint16(18, false);
      //           let minor = appleData.getUint16(20, false);
      //           let txPowerAt1m = -appleData.getInt8(22);
      //           let pathLossVs1m = txPowerAt1m - event.rssi;
      //       });

      //       console.log(data);
      //   })

      //TODO: Test bluetooth scanning 2
      // navigator.bluetooth.requestDevice({
      //       acceptAllDevices: true,
      //       optionalServices: ['battery_service']
      //    })
      //    .then(device => {
      //       console.log(device);
      //    })
      //    .catch(error => {
      //       console.log(error);
      //    });

      //TODO test bluetooth 3
      function testBLE() {
         // let filters = [];

         // let filterService = document.querySelector('#service').value;
         // if (filterService.startsWith('0x')) {
         //    filterService = parseInt(filterService);
         // }
         // if (filterService) {
         //    filters.push({
         //       services: [filterService]
         //    });
         // }

         // let filterName = document.querySelector('#name').value;
         // if (filterName) {
         //    filters.push({
         //       name: filterName
         //    });
         // }

         // let filterNamePrefix = document.querySelector('#namePrefix').value;
         // if (filterNamePrefix) {
         //    filters.push({
         //       namePrefix: filterNamePrefix
         //    });
         // }

         let options = {
            acceptAllDevices: true
         };
         // if (document.querySelector('#allDevices').checked) {
         //    options.acceptAllDevices = true;
         // } else {
         //    options.filters = filters;
         // }

         console.log('Requesting Bluetooth Device...');
         console.log('with ' + JSON.stringify(options));
         navigator.bluetooth.requestDevice(options)
            .then(device => {
               console.log('> Name:             ' + device.name);
               console.log('> Id:               ' + device.id);
               console.log('> Connected:        ' + device.gatt.connected);
            })
            .catch(error => {
               console.log('Argh! ' + error);
            });
      }

      document.getElementById('bleBtn').addEventListener('click', testBLE);

   }

})();
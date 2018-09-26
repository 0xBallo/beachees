'use strict';

const Moment = require('moment');

/**
 * Send push notification to user's devices and save to database
 * 
 * @param {*} admin 
 * @param {*} message 
 */
exports.send_push = (admin, db, user, message) => {
      // See documentation on defining a message payload.
      // https://firebase.google.com/docs/cloud-messaging/admin/send-messages

      let payload = message;
      const data = {
            user: user,
            notification: message.android.notification,
            date: Moment().format()
      }
      //INFO add notify to database
      db.notifies.insert(data, (err, newDoc) => {
            if (err) {
                  console.error(err);
            }
      });

      //INFO send push notification
      db.users.find({
            user: user
      }, (err, results) => {
            console.log(results);

            if (err) console.error(err);
            results.forEach(r => {
                  payload.token = r.token;
                  // Send a message to the device corresponding to the provided
                  // registration token.
                  admin.messaging().send(payload)
                        .then((response) => {
                              // Response is a message ID string.
                              console.log('Successfully sent message:', response);
                        })
                        .catch((error) => {
                              console.log('Error sending message:', error);
                        });
            });
      });

}

/**
 * Send push notifications to all registered devices
 * 
 * @param {*} admin 
 * @param {*} db 
 * @param {*} message 
 */
exports.send_push_broadcast = (admin, db, message) => {
      // See documentation on defining a message payload.
      // https://firebase.google.com/docs/cloud-messaging/admin/send-messages

      let payload = message;
      const data = {
            notification: message.android.notification,
            date: Moment().format()
      }
      //INFO add notify to database
      db.notifies.insert(data, (err, newDoc) => {
            if (err) {
                  console.error(err);
            }
      });

      //INFO send push notification
      db.users.find({}, (err, results) => {
            if (err) console.error(err);
            results.forEach(r => {
                  payload.token = r.token;
                  // Send a message to the device corresponding to the provided
                  // registration token.
                  admin.messaging().send(payload)
                        .then((response) => {
                              // Response is a message ID string.
                              console.log('Successfully sent message:', response);
                        })
                        .catch((error) => {
                              console.log('Error sending message:', error);
                        });
            });
      });
};
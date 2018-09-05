'use strict';
const url = require('url');

/**
 * Register User device for push notifications
 * 
 * @param {*} req body.user and body.token 
 * @param {*} res 
 * @param {*} db 
 */
exports.add_user_device = (req, res, db) => {
   const data = req.body;

   if (data.user === undefined || data.token === undefined) {
      res.status(401).send('Incomplete data in request!');
   } else {

      db.users.insert(data, function (err, newDoc) {
         if (err) {
            res.status(501).json(err);
         } else {
            res.status(201).json(newDoc);
         }
      });

   }
};

/**
 * TODO: debug method
 * Send notification to user's devices with message specified by request
 * 
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.send_push = (req, res, db, admin) => {
   const data = req.body;

   if (data.user === undefined || data.message === undefined) {
      res.status(401).send('Incomplete data in request!');
   } else {
      db.users.find({
         user: data.user
      }, (err, results) => {
         if (results.length == 0) {
            res.status(202).send('No device/user found!');
         } else {
            results.forEach(r => {
               //TODO: send push notification for all device
               //TODO: send push
               // This registration token comes from the client FCM SDKs.
               const registrationToken = 'fGF2s7xxIN8:APA91bEWl_6nXvYyLn3tm4Ks2kiQUj4WQMI4fdwSs4RDBMOEmrMYQtV5KXWy7sJnv-32p3OnKu9RW9bencesaI6ZqvOkCDRCLs9gKATBlSkvRpKMdc7SL0QJCs4TOGLzTaLGSb3YE8NN';
               //TODO: retrieve registration token from device

               // See documentation on defining a message payload.
               // https://firebase.google.com/docs/cloud-messaging/admin/send-messages
               const message = {
                  android: {
                     ttl: 3600 * 1000, // 1 hour in milliseconds
                     priority: 'normal',
                     notification: {
                        title: '$GOOG up 1.43% on the day',
                        body: '$GOOG gained 11.80 points to close at 835.67, up 1.43% on the day.',
                        icon: 'ic_notifications_black_24dp',
                        color: '#f45342'
                     }
                  },
                  token: registrationToken
               };

               // Send a message to the device corresponding to the provided
               // registration token.
               admin.messaging().send(message)
                  .then((response) => {
                     // Response is a message ID string.
                     console.log('Successfully sent message:', response);
                  })
                  .catch((error) => {
                     console.log('Error sending message:', error);
                  });

               //TODO: END

            });
            res.status(201).json(results);
         }
      });

   }

};

/**
 * Get notifications for specified user
 * 
 * @param {*} res 
 * @param {*} req 
 * @param {*} db 
 */
exports.get_notifications = (res, req, db) => {
   const urlParts = url.parse(req.url, true);
   const data = urlParts.query;

   if (data.user === undefined) {
      res.status(401).send('Incomplete data in request!');
   } else {

      db.users.find({
         user: data.user,
      }, (err, results) => {
         if (err)
            res.status(501).json(err);
         if (results.length == 0) {
            res.status(202).send('No notifies found!');
         } else {
            res.status(201).json(results);
         }
      });

   }
};

/**
 * Remove notification with specified id from datbase
 * 
 * @param {} res 
 * @param {*} req 
 * @param {*} db 
 */
exports.del_notification = (res, req, db) => {
   const data = req.body;

   if (data._id === undefined) {
      res.status(401).send('Identifiers not specified!');
   } else {
      db.users.remove({
         _id: data._id
      }, {}, function (err, numRemoved) {
         if (err)
            res.status(501).json(err);
         if (numRemoved === 0) {
            res.status(202).send('No document find with specified id!');
         } else {
            res.status(201).send('Correctly removed ' + numRemoved + ' document/s!');
         }
      });
   }
};
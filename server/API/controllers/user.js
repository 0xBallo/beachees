'use strict';
const url = require('url');
const Webpush = require('web-push');

/**
 * Register User device for push notifications
 * 
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.add_user_device = (req, res, db) => {
   const data = req.body;

   if (data.user === undefined || data.subscription === undefined) {
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
 * Send notification to user's devices with message specified by request
 * 
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.send_push = (req, res, db) => {
   const data = req.body;   

   if (data.user === undefined || data.message === undefined) {
      res.status(401).send('Incomplete data in request!');
   } else {
      const payload = JSON.stringify({
         message: data.message
      });

      db.users.find({
         user: data.user
      }, (err, results) => {
         if (results.length == 0) {
            res.status(402).send('No device/user found!');
         } else {
            results.forEach(r => {
               Webpush.sendNotification(r.subscription, payload)
                  .catch(error => {
                     console.error(error.stack);
                  });
            });
            res.status(201).json(results);
         }
      });


   }

};
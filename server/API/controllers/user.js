'use strict';
const url = require('url');
const Moment = require('moment');

exports.add_user_device = (req, res, db) => {
   let data = req.body;

   if (data.u === undefined || data.uid === undefined) {
      res.send('501: Incomplete data in request!');
   } else {
      // TODO: salvare la chiave per poter inviare notifiche al dispositivo
   }
};

exports.get_user_id = (req, res, db) => {
   const urlParts = url.parse(req.url, true);
   const parameters = urlParts.query;
   const user = parameters.user;

   if (user === undefined) {
      res.send('501: Incomplete data in request!');
   } else {
      //TODO: recuperare uid dal database
   }

};
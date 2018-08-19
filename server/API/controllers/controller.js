'use strict';
const url = require('url');
const Moment = require('moment');

exports.info = (req, res) => {
   res.send('Hello, welcome to root of APIv1');
}

/**
 * Get data from DHT collection in DB
 * 
 * @param {Datastore} db 
 * @param {*} req 
 * @param {*} res 
 */
exports.get_temp = (db, req, res) => {
   const urlParts = url.parse(req.url, true);
   const parameters = urlParts.query;
   const startDate = parameters.start;
   const endDate = parameters.end;

   if (startDate == undefined || endDate == undefined) {
      // INFO: retrieve all data
      db.dht.find({}, function (err, data) {
         res.json({
            data: data
         });
      });
   } else {
      // INFO: retrieve data between date
      db.dht.find({
         date: {
            $lt: end,
            $gt: start
         }
      }, function (err, data) {
         res.json({
            data: data
         });
      });
   }
};
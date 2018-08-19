'use strict';
const url = require('url');
const Datastore = require('nedb');

exports.info = (req, res) => {
   res.send('Hello, welcome to root of APIv1');
}

exports.get_temp = (db, req, res) => {
   const urlParts = url.parse(req.url, true);
   const parameters = urlParts.query;
   const startDate = parameters.start;
   const endDate = parameters.end;

   if (startDate == undefined || endDate == undefined) {
      db.dht.find({}, function (err, data) {
         res.json({
            data: data
         });
      });
   } else {
      //TODO: retrieve data from DHT table in db between start and end date
      db.dht.find({}, function (err, data) {
         res.json({
            data: data
         });
      });
   }
};
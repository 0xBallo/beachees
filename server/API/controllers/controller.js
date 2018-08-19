'use strict';
const url = require('url');
const Datastore = require('nedb');

exports.info = (req, res) => {
   res.send('Hello, welcome to root of APIv1');
}

exports.get_temp = function (db, req, res) {
   const urlParts = url.parse(req.url, true);
   const parameters = urlParts.query;
   const startDate = parameters.start;
   const endDate = parameters.end;
   let myResponse = '';

   if (startDate == undefined || endDate == undefined) {
      //TODO: retrieve all data from DHT
      db.find({}, function (err, dht) {
          myResponse = dht;
          res.json({
            message: myResponse
         });
      });
   } else {
      //TODO: retrieve data from DHT table in db between start and end date
      myResponse = 'Start Date is ' + startDate + ' and End Date is ' + endDate;
   }
};
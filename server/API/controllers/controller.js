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
exports.get_temp = (req, res, db) => {
   const urlParts = url.parse(req.url, true);
   const parameters = urlParts.query;
   const startDate = parameters.start;
   const endDate = parameters.end;

   if (startDate == undefined || endDate == undefined) {
      // INFO: retrieve all data
      db.beach.find({}, function (err, data) {
         res.json({
            data: data
         });
      });
   } else {
      // INFO: retrieve data between date
      db.beach.find({
         $where: function () {
            return Moment(this.date).isBetween(Moment(startDate), Moment(endDate));
         }
      }, function (err, data) {
         res.json({
            data: data
         });
      });
   }
};

// INFO: add temperature to database
exports.add_temp = (req, res, db) => {
   let data = req.body;

   //DEBUG:
   console.log(data);

   let dht = {
      user: data.u,
      humidity: data.h,
      temperature: data.t,
      date: new Date()
   };
   db.beach.insert(dht, function (err, newDoc) {});

   res.end();
}
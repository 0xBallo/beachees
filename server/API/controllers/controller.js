'use strict';
const url = require('url');
const Moment = require('moment');

const DHT = 0;
const UVA = 1;

/**
 * Info about API
 * 
 * @param {*} req 
 * @param {*} res 
 */
exports.info = (req, res) => {
   res.send('Hello, welcome to root of APIv1');
}

/**
 * Get user data DHT from collection
 * 
 * @param {Datastore} db 
 * @param {*} req 
 * @param {*} res 
 */
exports.get_temp_hum = (req, res, db) => {
   const urlParts = url.parse(req.url, true);
   const parameters = urlParts.query;
   const user = urlParts.user;
   const startDate = parameters.start;
   const endDate = parameters.end;

   if (user == undefined) {
      res.send("Username not specified!!");
   } else {
      if (startDate == undefined || endDate == undefined) {
         // INFO: retrieve all data
         db.beach.find({
            user: user,
            type: DHT
         }, function (err, data) {
            res.json({
               data: data
            });
         });
      } else {
         // INFO: retrieve data between date
         db.beach.find({
            user: user,
            type: DHT,
            $where: function () {
               return Moment(this.date).isBetween(Moment(startDate), Moment(endDate));
            }
         }, function (err, data) {
            res.json({
               data: data
            });
         });
      }
   }
};

/**
 * Add temperature and humidity to datastore
 * 
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.add_temp_hum = (req, res, db) => {
   let data = req.body;

   if (data.u == undefined || data.h == undefined || data.t == undefined) {
      res.json({
         message: data,
         error: "Incomplete data!"
      });
   } else {
      let dht = {
         user: data.u,
         type: DHT,
         humidity: data.h,
         temperature: data.t,
         date: new Date()
      };
      db.beach.insert(dht, function (err, newDoc) {
         res.json({
            message: newDoc,
            error: err.toString()
         });
      });
   }

}

/**
 * Get User UVA data from collection
 * 
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.get_uva = (req, res, db) => {
   const urlParts = url.parse(req.url, true);
   const parameters = urlParts.query;
   const user = urlParts.user;
   const startDate = parameters.start;
   const endDate = parameters.end;

   if (user == undefined) {
      res.send("Username not specified!!");
   } else {
      if (startDate == undefined || endDate == undefined) {
         // INFO: retrieve all data
         db.beach.find({
            user: user,
            type: UVA
         }, function (err, data) {
            res.json({
               data: data
            });
         });
      } else {
         // INFO: retrieve data between date
         db.beach.find({
            user: user,
            type: UVA,
            $where: function () {
               return Moment(this.date).isBetween(Moment(startDate), Moment(endDate));
            }
         }, function (err, data) {
            res.json({
               data: data
            });
         });
      }
   }
}

/**
 * Add User UVA data to collection
 * 
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.add_uva = (req, res, db) => {
   let data = req.body;

   if (data.u == undefined || data.l == undefined) {
      res.json({
         message: data,
         error: "Incomplete data!"
      });
   } else {
      let dht = {
         user: data.u,
         type: UVA,
         uva: data.l,
         date: new Date()
      };
      db.beach.insert(dht, function (err, newDoc) {
         res.json({
            message: newDoc,
            error: err.toString()
         });
      });
   }

}
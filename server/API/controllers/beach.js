'use strict';
const url = require('url');
const Moment = require('moment');

//========================= BEACH ====================================

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
   const user = parameters.user;
   const date = parameters.date;

   if (user == undefined) {
      res.send("Username not specified!!");
   } else {
      let queryDate;
      if (date == undefined) {
         // INFO: retrieve data of today
         queryDate = Moment().format('YYYY-MM-DD');
      } else {
         // INFO: retrieve data from a day
         queryDate = Moment(date).format('YYYY-MM-DD');
      }
      db.beach.find({
            user: user,
            temperature: {
               $exists: true
            },
            humidity: {
               $exists: true
            },
            date: queryDate,
            $where: function () {
               let hour = parseInt(this.hour);
               return hour >= 8 && hour <= 19;
            }
         })
         .sort({
            hour: 1
         })
         .exec(function (err, data) {
            let temp = 0,
               result = [];
            let sum_h = 0.0,
               sum_t = 0.0,
               count = 0;
            //console.log(data, err);
            if (err) {
               res.send(err);
            } else {
               data.forEach(el => {
                  if (temp !== parseInt(el.hour)) {
                     if (count !== 0) {
                        result.push({
                           temperature: sum_t / parseFloat(count),
                           humidity: sum_h / parseFloat(count),
                           hour: temp
                        });
                     }
                     temp = parseInt(el.hour);
                     count = 0;
                     sum_t = 0.0;
                     sum_h = 0.0;
                  }
                  sum_t += parseFloat(el.temperature);
                  sum_h += parseFloat(el.humidity);
                  count++;
               });
               res.json({
                  data: result
               });
            }
         });

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
         humidity: data.h,
         temperature: data.t,
         date: Moment().format('YYYY-MM-DD'),
         hour: Moment().format('HH'),
         ISO: Moment().format()
      };
      db.beach.insert(dht, function (err, newDoc) {
         res.json({
            message: newDoc,
            error: err
         });
      });
   }

}

/**
 * Get last data from DHT
 * 
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.get_dht_now = (req, res, db) => {
   const urlParts = url.parse(req.url, true);
   const parameters = urlParts.query;
   const user = parameters.user;

   if (user == undefined) {
      res.send("Username not specified!!");
   } else {
      db.beach.find({
            user: user,
            temperature: {
               $exists: true
            },
            humidity: {
               $exists: true
            }
         })
         .sort({
            ISO: -1
         })
         .limit(1)
         .exec(function (err, data) {
            if (err) {
               res.send(err);
            } else {
               res.json({
                  data: data
               });
            }
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
   const user = parameters.user;
   const date = parameters.date;

   if (user == undefined) {
      res.send("Username not specified!!");
   } else {
      let queryDate;
      if (date == undefined) {
         // INFO: retrieve data of today
         queryDate = Moment().format('YYYY-MM-DD');
      } else {
         // INFO: retrieve data from a day
         queryDate = Moment(date).format('YYYY-MM-DD');
      }
      db.beach.find({
            user: user,
            uva: {
               $exists: true
            },
            date: queryDate,
            $where: function () {
               let hour = parseInt(this.hour);
               return hour >= 8 && hour <= 19;
            }
         })
         .sort({
            hour: 1
         })
         .exec(function (err, data) {
            let temp = 0,
               result = [];
            let sum_t = 0.0,
               count = 0;
            data.forEach(el => {
               if (temp !== parseInt(el.hour)) {
                  if (count !== 0) {
                     result.push({
                        uva: sum_t / parseFloat(count),
                        hour: temp
                     });
                  }
                  temp = parseInt(el.hour);
                  count = 0;
                  sum_t = 0.0;
               }
               sum_t += parseFloat(el.uva);
               count++;
            });
            res.json({
               data: result
            });
         });
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
         uva: data.l,
         date: Moment().format('YYYY-MM-DD'),
         hour: Moment().format('HH'),
         ISO: Moment().format()
      };
      db.beach.insert(dht, function (err, newDoc) {
         res.json({
            message: newDoc,
            error: err
         });
      });
   }

}

/**
 * Get last data from UVA sensor
 * 
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.get_uva_now = (req, res, db) => {
   const urlParts = url.parse(req.url, true);
   const parameters = urlParts.query;
   const user = parameters.user;

   if (user == undefined) {
      res.send("Username not specified!!");
   } else {
      db.beach.find({
            user: user,
            uva: {
               $exists: true
            }
         })
         .sort({
            ISO: -1
         })
         .limit(1)
         .exec(function (err, data) {
            if (err) {
               res.send(err);
            } else {
               res.json({
                  data: data
               });
            }
         });
   }
}
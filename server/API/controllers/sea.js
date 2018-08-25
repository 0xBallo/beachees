'use strict';
const url = require('url');
const Moment = require('moment');

//==================================== SEA ====================

/**
 * Get Water Temperature for sea
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.get_water_temp = (req, res, db) => {
   const urlParts = url.parse(req.url, true);
   const parameters = urlParts.query;
   const date = parameters.date;

   let queryDate;
   if (date == undefined) {
      // INFO: retrieve data of today
      queryDate = Moment().format('YYYY-MM-DD');
   } else {
      // INFO: retrieve data from a day
      queryDate = Moment(date).format('YYYY-MM-DD');
   }
   db.sea.find({
         user: user,
         watertemp: {
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
                     watertemp: sum_t / parseFloat(count),
                     hour: temp
                  });
               }
               temp = parseInt(el.hour);
               count = 0;
               sum_t = 0.0;
            }
            sum_t += parseFloat(el.watertemp);
            count++;
         });
         res.json({
            data: result
         });
      });
}


/**
 * Add water temperature to collection
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.add_water_temp = (req, res, db) => {
   let data = req.body;

   if (data.t == undefined) {
      res.json({
         message: data,
         error: "Incomplete data!"
      });
   } else {
      let obj = {
         watertemp: data.t,
         date: Moment().format('YYYY-MM-DD'),
         hour: Moment().format('HH'),
         ISO: Moment().format()
      };
      db.sea.insert(obj, function (err, newDoc) {
         res.json({
            message: newDoc,
            error: err
         });
      });
   }
}

/**
 * Get last data from Sea Temperature sensor
 * 
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.get_water_temp_now = (req, res, db) => {
   const urlParts = url.parse(req.url, true);
   const parameters = urlParts.query;
   db.beach.find({
         watertemp: {
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


/**
 * Get water turbidity from collection
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.get_water_turb = (req, res, db) => {
   const urlParts = url.parse(req.url, true);
   const parameters = urlParts.query;
   const date = parameters.date;

   let queryDate;
   if (date == undefined) {
      // INFO: retrieve data of today
      queryDate = Moment().format('YYYY-MM-DD');
   } else {
      // INFO: retrieve data from a day
      queryDate = Moment(date).format('YYYY-MM-DD');
   }
   db.sea.find({
         user: user,
         turbidity: {
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
                     turbidity: sum_t / parseFloat(count),
                     hour: temp
                  });
               }
               temp = parseInt(el.hour);
               count = 0;
               sum_t = 0.0;
            }
            sum_t += parseFloat(el.turbidity);
            count++;
         });
         res.json({
            data: result
         });
      });
}


/**
 * Add water turbidity to collection
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.add_water_turb = (req, res, db) => {
   let data = req.body;

   if (data.t == undefined) {
      res.json({
         message: data,
         error: "Incomplete data!"
      });
   } else {
      let obj = {
         turbidity: data.t,
         date: Moment().format('YYYY-MM-DD'),
         hour: Moment().format('HH'),
         ISO: Moment().format()
      };
      db.sea.insert(obj, function (err, newDoc) {
         res.json({
            message: newDoc,
            error: err
         });
      });
   }
}

/**
 * Get last data from Sea Temperature sensor
 * 
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.get_water_turb_now = (req, res, db) => {
   const urlParts = url.parse(req.url, true);
   const parameters = urlParts.query;
   db.beach.find({
         turbidity: {
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


/**
 * Get waves movement from gyroscope and accelerometer from collection
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.get_waves_acc = (req, res, db) => {
   const urlParts = url.parse(req.url, true);
   const parameters = urlParts.query;
   const date = parameters.date;

   let queryDate;
   if (date == undefined) {
      // INFO: retrieve data of today
      queryDate = Moment().format('YYYY-MM-DD');
   } else {
      // INFO: retrieve data from a day
      queryDate = Moment(date).format('YYYY-MM-DD');
   }
   db.sea.find({
         user: user,
         waves: {
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
         data.forEach(el => {
            if (temp !== parseInt(el.hour)) {
               if (count !== 0) {
                  result.push({
                     acc: sum_t / parseFloat(count),
                     gyro: sum_h / parseFloat(count),
                     hour: temp
                  });
               }
               temp = parseInt(el.hour);
               count = 0;
               sum_t = 0.0;
               sum_h = 0.0;
            }
            sum_t += parseFloat(el.acc);
            sum_h += parseFloat(el.gyro);
            count++;
         });
         res.json({
            data: result
         });
      });
}


/**
 * TODO: Add waves movement and data to collection
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.add_waves_acc = (req, res, db) => {
   let data = req.body;

   if (data.a == undefined || data.g == undefined) {
      res.json({
         message: data,
         error: "Incomplete data!"
      });
   } else {
      let obj = {
         //TODO impostare waves
         waves: 'calmo|mosso|molto mosso',
         acc: data.a,
         gyro: data.g,
         date: Moment().format('YYYY-MM-DD'),
         hour: Moment().format('HH'),
         ISO: Moment().format()
      };
      db.sea.insert(obj, function (err, newDoc) {
         res.json({
            message: newDoc,
            error: err
         });
      });
   }
}

/**
 * Get last data from Sea Temperature sensor
 * 
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.get_waves_now = (req, res, db) => {
   const urlParts = url.parse(req.url, true);
   const parameters = urlParts.query;
   db.beach.find({
         acc: {
            $exists: true
         },
         gyro: {
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
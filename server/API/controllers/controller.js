'use strict';
const url = require('url');
const Moment = require('moment');

/**
 * Info about API
 * 
 * @param {*} req 
 * @param {*} res 
 */
exports.info = (req, res) => {
    res.send('Hello, welcome to root of APIv1');
}

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
                        console.log(temp, result, sum_h, sum_t, count);
                        
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
                    sum_t += el.uva;
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
            temperature: {
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
                            temperature: sum_t / parseFloat(count),
                            hour: temp
                        });
                    }
                    temp = parseInt(el.hour);
                    count = 0;
                    sum_t = 0.0;
                }
                sum_t += el.temperature;
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
            temperature: data.t,
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
                sum_t += el.turbidity;
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
                sum_t += el.acc;
                sum_h += el.gyro;
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
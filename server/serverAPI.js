const Express = require('express');
const Cors = require('cors');
const Datastore = require('nedb');
const Parser = require('body-parser');
const Webpush = require('web-push');
const app = Express();
const router = Express.Router();
const Route = require('./API/routes/route');

const port = process.env.PORT || 3001;
// Keys for Web Push notifications
const publicVapidKey = process.env.PUBLIC_VAPID_KEY;
const privateVapidKey = process.env.PRIVATE_VAPID_KEY;

// DATABASE
let lidoBase = {};
lidoBase.beach = new Datastore('lidoBase-beach.db');
lidoBase.sea = new Datastore('lidoBase-sea.db');
lidoBase.users = new Datastore('lidoBase-users.db');

lidoBase.sea.loadDatabase();
lidoBase.beach.loadDatabase();
lidoBase.users.loadDatabase();

Webpush.setVapidDetails('mailto:mattia.ballo@studio.unibo.it', publicVapidKey, privateVapidKey);

// ALLOW CORS request
app.use(Cors());

// Expose API on localhost:3001
app.get('/', (request, response) => response.send('Beachees Main Server'));

app.use(Parser.json());

// all routes prefixed with /api
app.use('/api', router);

Route(router, lidoBase);

// set the server to listen on port 3001
app.listen(port, () => console.log('Listening on port', port));
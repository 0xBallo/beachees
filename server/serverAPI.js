const Express = require('express');
const Cors = require('cors');
const Datastore = require('nedb');
const Parser = require('body-parser');
const Admin = require("firebase-admin");

const app = Express();
const router = Express.Router();
const Route = require('./API/routes/route');
const Debug = require('./debug');

const serviceAccount = require("./config/beachees-980a4-firebase.json");

const port = process.env.PORT || 3001;




// DATABASE
let lidoBase = {};
lidoBase.beach = new Datastore({
   filename: 'lidoBase-beach.db',
   autoload: false
});
lidoBase.sea = new Datastore({
   filename: 'lidoBase-sea.db',
   autoload: false
});
lidoBase.users = new Datastore({
   filename: 'lidoBase-users.db',
   autoload: false
});
lidoBase.notifies = new Datastore({
   filename: 'lidoBase-notifies.db',
   autoload: false
});

lidoBase.sea.loadDatabase();
lidoBase.beach.loadDatabase();
lidoBase.users.loadDatabase();
lidoBase.notifies.loadDatabase();

// Initialize Firebase Admin services
Admin.initializeApp({
   credential: Admin.credential.cert(serviceAccount),
   databaseURL: "https://beachees-980a4.firebaseio.com"
});

// ALLOW CORS request
app.use(Cors());

// Expose API on localhost:3001
app.get('/', (request, response) => response.send('Beachees Main API Server'));

app.use(Parser.json());

// all routes prefixed with /api
app.use('/api', router);

Route(router, lidoBase, Admin);

if (process.env.DEBUG) {
   console.log('DEBUG MODE enabled!');
   Debug.populate_db(lidoBase);
}

// set the server to listen on port 3001
app.listen(port, () => console.log('Listening on port', port));
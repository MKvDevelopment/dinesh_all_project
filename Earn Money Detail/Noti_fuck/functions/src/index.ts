"use-strict"
import functions = require("firebase-functions");
import admin = require("firebase-admin");
admin.initializeApp(functions.config().firebase);

exports.makeUppercase = functions.database.ref("/Notifications/{pushId}/{id}")
    .onCreate((snapshot, context) => {
        const tokenResult=snapshot.val().token;
      
        const payload = {
            notification:{
                title: "New mesaage received. Open app and go to chat option.",
                text: "New mesaage received. Open app and go to chat option.", 
            } };
            return admin.messaging().sendToDevice(tokenResult, payload);
         
    });

var express = require('express');
var http    = require('http');
var fs      = require('fs');
var url     = require('url');
var path    = require('path');
var status  = require('./http_status.js');
var mysql   = require('mysql');
var app     = express();

//Create a connection object with the user details
var connectionPool = mysql.createPool({
    connectionLimit: 1,
    host: "localhost",
    port: 3307,
    user: "root",
    password: "root",
    database: "price_comparison_website",
    debug: false
});


//Set up the application to handle GET requests sent to the user path
app.get('/monitors/*', handleGetRequest);//Subfolders
app.get('/monitors', handleGetRequest);



//Serve up static pages from public folder
app.use(express.static('public'));

/* Handles GET requests sent to web service.
   Processes path and query string and calls appropriate functions to
   return the data. */
   function handleGetRequest(request, response){

    //Parse the URL
    var urlObj = url.parse(request.url, true);

    //Extract object containing queries from URL object.
    var queries = urlObj.query;

    //Get the pagination properties if they have been set. Will be  undefined if not set.
    var numItems = queries['num_items'];
    var offset = queries['offset'];
    
    //Split the path of the request into its components
    var pathArray = urlObj.pathname.split("/");

    //Get the last part of the path
    var pathEnd = pathArray[pathArray.length - 1];

    //If path ends with 'models' we return all components
    if(pathEnd === 'monitors'){
        getAllMonitorsCount(response, numItems, offset, pathEnd);
        return;
    }

    //If path ends with 'models/', we return all components
    if (pathEnd === '' && pathArray[pathArray.length - 2] === 'monitors'){
        getAllMonitorsCount(response, numItems, offset, pathEnd);
        return;
    }

    //RegEx
    var regEx = new RegExp('.*?');
    if(regEx.test(pathEnd)){
        getSpecificMonitorCount(response, numItems, offset, pathEnd);;
        return;
    }

    //The path is not recognized. Return an error message
    response.status(HTTP_STATUS.NOT_FOUND);
    response.send("{error: 'Path not recognized', url: " + request.url + "}");
}


/** Returns all of the models, possibly with a limit on the total number of items returned and the offset (to
 *  enable pagination). This function should be called in the callback of getTotalModelsCount  */
function getAllMonitors(response, totNumItems, numItems, offset, pathEnd){
    //Select the model data using JOIN to convert foreign keys into useful data.
    let sql = "SELECT monitors.id, monitors.model, monitors.manufacturer, monitors.description, monitors.image_url, comparison.price, comparison.url " +
    "FROM (monitors INNER JOIN comparison ON monitors.id = comparison.monitor_id)";

    //Limit the number of results returned, if this has been specified in the query string
    if(numItems !== undefined && offset !== undefined ){
        sql += "ORDER BY moniotrs.id LIMIT " + numItems + " OFFSET " + offset;
    }

    //Execute the query
    connectionPool.query(sql, function (err, result) {

        //Check for errors
        if (err){
            //Not an ideal error code, but we don't know what has gone wrong.
            response.status(HTTP_STATUS.INTERNAL_SERVER_ERROR);
            response.json({'error': true, 'message': + err});
            return;
        }

        //Create JavaScript object that combines total number of items with data
        var returnObj = {totNumItems: totNumItems};
        returnObj.monitors = result; //Array of data from database

        //Return results in JSON format
        response.json(returnObj);
    });
}

function getAllMonitorsCount(response, numItems, offset, pathEnd){
    var tmpPathEnd = pathEnd;
    var sql = "SELECT COUNT(*) FROM monitors";

    //Execute the query and call the anonymous callback function.
    connectionPool.query(sql, function (err, result) {

        //Check for errors
        if (err){
            //Not an ideal error code, but we don't know what has gone wrong.
            response.status(HTTP_STATUS.INTERNAL_SERVER_ERROR);
            response.json({'error': true, 'message': + err});
            return;
        }

        //Get the total number of items from the result
        var totNumItems = result[0]['COUNT(*)'];

        //Call the function that retrieves all cereals
        getAllMonitors(response, totNumItems, numItems, offset, tmpPathEnd);
    });
}


function getSpecificMonitorCount(response, numItems, offset, pathEnd){
    var sql = "";
    var tmpPathEnd = pathEnd;
    if (tmpPathEnd !== undefined){
        var find = tmpPathEnd.replace(/%20/g, " ");
        sql = "SELECT COUNT(*) FROM monitors WHERE upper(monitors.manufacturer) LIKE " + "'%" + find.toUpperCase() + "%'";
    }
    //Execute the query and call the anonymous callback function.
    connectionPool.query(sql, function (err, result) {

        //Check for errors
        if (err){
            //Not an ideal error code, but we don't know what has gone wrong.
            response.status(HTTP_STATUS.INTERNAL_SERVER_ERROR);
            response.json({'error': true, 'message': + err});
            return;
        }

        //Get the total number of items from the result
        var totNumItems = result[0]['COUNT(*)'];

        //Call the function that retrieves searched product
        getSpecificMonitor(response, totNumItems, numItems, offset, tmpPathEnd)
    });
}


/** Returns searched  computer componet */
function getSpecificMonitor(response, totNumItems, numItems, offset, pathEnd){
    var sql ="";

    if (pathEnd !== undefined){ 
        var find = pathEnd.replace(/%20/g, " ");
        //Build SQL query to select computer componet.
         
            sql = "SELECT monitors.id, monitors.model, monitors.manufacturer, monitors.description, monitors.image_url, comparison.price, comparison.url " +
            "FROM (monitors INNER JOIN comparison ON monitors.id = comparison.monitor_id)" +
            "WHERE upper(monitors.model) LIKE" + "'%" + find.toUpperCase() + "%'";

    }

    //Limit the number of results returned, if this has been specified in the query string
    if(numItems !== undefined && offset !== undefined ){
        sql += "ORDER BY monitors.id LIMIT " + numItems + " OFFSET " + offset;
    }

    //Execute the query
    connectionPool.query(sql, function (err, result) {

        //Check for errors
        if (err){
            //Not an ideal error code, but we don't know what has gone wrong.
            response.status(HTTP_STATUS.INTERNAL_SERVER_ERROR);
            response.json({'error': true, 'message': + err});
            return;
        }

        //Create JavaScript object that combines total number of items with data
        var returnObj = {totNumItems: totNumItems};
        returnObj.monitors = result; //Array of data from database

        //Return results in JSON format
        response.json(returnObj);
    });
}

var server = app.listen(8080, function () {
    var port = server.address().port
    console.log("Server is running at http://localhost:" + port)
})

module.exports = server;
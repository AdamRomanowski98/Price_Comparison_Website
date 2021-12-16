const chai = require("chai");
const chaiHttp = require("chai-http");
const server = require("../server");
const should = chai.should();
const { assert } = require('chai')
chai.use(chaiHttp);




//Wrapper for all server tests
describe("Server", function () {
  //Mocha/Chai test of RESTful Web Service
  describe("Database connection", () => {
    it("shoul get all monitors", (done) => {
      chai
        .request(server)
        .get("/monitors")
        .end((err, res) => {
          res.should.have.status(200);
          done();
        });
    });
  });


  //Mocha/Chai test of RESTful Web Service
  describe("GET /monitors", () => {
    it("should get monitors object", (done) => {
      chai
        .request(server)
        .get("/monitors")
        .end((err, res) => {
          JSON.parse(res.text).should.be.a("object");
          done();
        });
    });
  });


   //Mocha/Chai test of RESTful Web Service
   describe("GET /comparison", () => {
    it("should get all comparison object", (done) => {
      chai
        .request(server)
        .get("/monitors")
        .end((err, res) => {
          JSON.parse(res.text).should.be.a("object");
          done();
        });
    });
  });

});

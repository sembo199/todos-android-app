'use strict';
var mongoose = require('mongoose'),
  Todo = mongoose.model('Todos');

exports.list_all_Todos = function(req, res) {
  Todo.find({}, function(err, Todo) {
    if (err)
      res.send(err);
    res.json(Todo);
  });
};

exports.create_a_Todo = function(req, res) {
  var new_Todo = new Todo(req.body);
  new_Todo.save(function(err, Todo) {
    if (err)
      res.send(err);
    res.json(Todo);
  });
};

exports.read_a_Todo = function(req, res) {
  Todo.findById(req.params.TodoId, function(err, Todo) {
    if (err)
      res.send(err);
    res.json(Todo);
  });
};

exports.update_a_Todo = function(req, res) {
  Todo.findOneAndUpdate(req.params.TodoId, req.body, {new: true}, function(err, Todo) {
    if (err)
      res.send(err);
    res.json(Todo);
  });
};

exports.delete_a_Todo = function(req, res) {
  Todo.remove({
    _id: req.params.TodoId
  }, function(err, Todo) {
    if (err)
      res.send(err);
    res.json({ message: 'Todo successfully deleted' });
  });
};

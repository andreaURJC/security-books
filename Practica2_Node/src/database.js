const mongoose = require('mongoose');
const url = "mongodb://localhost:27017/booksDB";
const User = require('./models/user.js').User;
const Book = require('./models/book.js').Book;
const Role = require('./models/role').Role;


async function connect() {

    await mongoose.connect(url, {
        useUnifiedTopology: true,
        useNewUrlParser: true,
        useFindAndModify: false
    });

    console.log("Connected to Mongo");

    await init();
}

async function disconnect() {
    await mongoose.connection.close();
    console.log("Disconnected from MongoDB")
}

async function init() {

    console.log('Initializing database');

    console.log('Populating database with roles');

    await Role.deleteMany({});

    const adminRole = await new Role({
        _id: new mongoose.Types.ObjectId("60042695f045e6f5a218009a"),
        name: "ADMIN"
    }).save();

    const userRole = await new Role({
        _id: new mongoose.Types.ObjectId("600426b9f045e6f5a21800b0"),
        name: "USER"
    }).save();

    console.log('Populating database with users');

    await User.deleteMany({});

    const user1 = await new User({
        _id: new mongoose.Types.ObjectId("5fda3234e9e3fd53e3907bed"),
        nick: "user1",
        password: "$2a$10$XURPShQNCsLjp1ESc2laoObo9QZDhxz73hJPaEv7/cBha4pk0AgP.",
        email: "user1@email.es"
    }).save();

    user1.roles.push(adminRole._id);
    user1.roles.push(userRole._id);

    await user1.save();

    const user2 = await new User({
        _id: new mongoose.Types.ObjectId("5fda3234e9e3fd53e3907bef"),
        nick: "user2",
        password: "$2a$10$XURPShQNCsLjp1ESc2laoObo9QZDhxz73hJPaEv7/cBha4pk0AgP.",
        email: "user2@email.es"
    }).save();

    user2.roles.push(userRole._id);

    await user2.save();

    console.log('Populating database with books');

    await Book.deleteMany({});

    await new Book({
        _id: new mongoose.Types.ObjectId("5fda3234e9e3fd53e3907bf0"),
        title: "Book 1 title",
        summary: "Book 1 summary",
        author: "Book 1 author",
        publisher: "Book 1 publisher",
        publicationYear: 1992
    }).save();

    const book2 = await new Book({
        _id: new mongoose.Types.ObjectId("5fda350d3749aa4832165b84"),
        title: "Book 2 title",
        summary: "Book 2 summary",
        author: "Book 2 author",
        publisher: "Book 2 publisher",
        publicationYear: 2006
    }).save();

    console.log('Populating database with comments');

    book2.comments.push({
        _id: new mongoose.Types.ObjectId("5fdb4812df5c2555a401b6da"),
        comment: "Book 2 comment 1 from user 1",
        score: 2.6,
        user: new mongoose.Types.ObjectId("5fda3234e9e3fd53e3907bed")
    });
    book2.comments.push({
        _id: new mongoose.Types.ObjectId("5fdb4812df5c2555a401b6db"),
        comment: "Book 2 comment 2 from user 1",
        score: 4,
        user: new mongoose.Types.ObjectId("5fda3234e9e3fd53e3907bed")
    });

    await book2.save();

    console.log('Database initialized');
}

module.exports = { connect, disconnect }
Qubuto
=====================================

Qubuto offers a collaborative way of managing projects with todolists, conversations, sharing. Check out an instance of Qubuto running on [qubuto.com](http://qubuto.com).

### Collaboration

The way Qubuto has been thought is that an action done in a project is immediatly visible in other users window. No need to reload anything, notifications and quick animations show the changes done in the todolists and conversations.

![Sharing a project with other users](http://remy.io/files/qubuto/collaborators.png)

### Todolists

The todolists are the main feature of Qubuto.

They're focused on simplicity. It's a mess when you have ten thousands fields to fill to be able to add a task in your todolist. In Qubuto, you just enter a title and a content, and your task can be created. Afterwhile, you can apply one of the three available and configurable tags on your task or add comments.

![Task in Qubuto](http://remy.io/files/qubuto/todolist.png)

Once done, you check it and it goes underneath to the archived tasks. While working on your todolists, every actions are dynamically applied into your collaborators window, thanks to the power of WebSockets.

Filters allow you to quickly search for tasks waiting to be done.

![Filters in Qubuto](http://remy.io/files/qubuto/filters.png)

### Conversations

Conversations are kind of a forum thread, but entirely rendered in Markdown for text formatting and with collaborative support, such as the todolists.

They should be used to write down ideas, roadmaps or even to discuss on features.

## Technologies

Qubuto is wrote in Java with the [Play Framework!](http://www.playframework.com/) 2 for the back-end part. Data is stored in MongoDB using [Jongo](http://jongo.org/) and this [Play2 module](https://github.com/bguerout/jongo-play2-spike).

## Roadmap

  * Files uploading : the file sharing per-project,
  * Tasks archiving : remove tasks from a todolist by archiving them,
  * The ability to move tasks between todolists in a project,
  * Todolists / conversations archives,
  * Connect with GitHub issues,
  * Permissions management in projects


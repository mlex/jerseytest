package de.codecentric.mjl.jerseytest.services;

import java.util.ArrayList;
import java.util.List;

import de.codecentric.mjl.jerseytest.exceptions.TodoNotFoundException;

public class TodoService {

    List<String> todos = new ArrayList<String>();

    public List<String> getAllTodos() {
	return new ArrayList<String>(todos);
    }

    public void addTodo(String todo) {
	todos.add(todo);
    }

    public void removeTodo(String todo) {
	if (todos.remove(todo) == false) {
	    throw new TodoNotFoundException("Todo '" + todo + "' not found.");
	}
    }
}

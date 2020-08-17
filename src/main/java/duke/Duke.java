package duke;

import exception.*;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Duke {
    Scanner sc;
    String input;
    List<Task> todoList;

    public Duke() {
        sc = new Scanner(System.in);
        String input;
        todoList = new ArrayList<>();
    }

    public void start() {
        Duke.hello();
        handleInteraction();
        Duke.bye();
    }

    public void handleInteraction() {
        while (sc.hasNextLine()) {
            buildChatFence();
            input = sc.nextLine();
            // stopping condition
            if (input.equals("bye")) {
                break;
            }

            // handle commands
            try {
                String[] parsed = parseInput(input);
                Commands command;

                try {
                    // modify parsed[0] to uppercase to ensure that comparison with the commands enum is standardized
                    command = Commands.valueOf(parsed[0].toUpperCase());
                } catch (IllegalArgumentException ex){
                    // if parsed[0] not amongst valid commands, will throw an IllegalArgumentException
                    throw new UnknownCommandException();
                }

                switch (command) {
                    case LIST:
                        // we ignore the argument after `list`.
                        System.out.println("     Here are the tasks in your list:");
                        for (int i = 0; i < todoList.size(); i++) {
                            System.out.printf("     %d. %s%n", i + 1, todoList.get(i).showTask());
                        }
                        break;
                    case DONE:
                        try {
                            int taskNumber = Integer.parseInt(parsed[1]) - 1;
                            // Check that the task number makes sense.
                            if (taskNumber >= 0 && taskNumber < todoList.size()) {
                                todoList.get(taskNumber).markAsDone();
                                System.out.println("     Good job! I've marked this task as done:");
                                System.out.printf("      %s%n", todoList.get(taskNumber).showTask());
                            } else {
                                System.out.println("     Sorry, I can't find it in your list!");
                            }
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                            throw new InvalidUsageException("Usage: done <task number>");
                        }
                        break;
                    case DELETE:
                        try {
                            int taskNumber = Integer.parseInt(parsed[1]) - 1;
                            // Check that the task number makes sense.
                            if (taskNumber >= 0 && taskNumber < todoList.size()) {
                                System.out.println("     Noted. I've removed this task: ");
                                System.out.printf("      %s%n", todoList.remove(taskNumber).showTask());
                                System.out.printf("     Now you have %d %s in the list%n",
                                        todoList.size(), todoList.size() > 1 ? "tasks" : "task");
                            } else {
                                System.out.println("     Sorry, I can't find it in your list!");
                            }
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                            throw new InvalidUsageException("Usage: delete <task number>");
                        }
                        break;
                    case TODO:
                        try {
                            this.addTodo(parsed[1]);
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            throw new TodoInvalidUsageException("Todo description cannot be empty.");
                        }
                        break;
                    case DEADLINE:
                        try {
                            this.addDeadline(parsed[1]);
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            throw new DeadlineInvalidUsageException("Deadline description cannot be empty.");
                        }
                        break;
                    case EVENT:
                        try {
                            this.addEvent(parsed[1]);
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            throw new EventInvalidUsageException("Event description cannot be empty.");
                        }
                        break;
                    default:
                        throw new UnknownCommandException();
                }
            } catch (InvalidUsageException | UnknownCommandException ex) {
                System.out.println(ex.getMessage());
            }
            buildChatFence();
        }
    }

    private static void hello() {
        buildChatFence();
        System.out.println("     Hellowww!! I'm Alexa, your personal todo manager!");
        System.out.println("     How can I help you today?");
        buildChatFence();
    }

    private static void bye() {
        buildChatFence();
        System.out.println("     Bye? I hope it's not forever! Come back soon!");
        buildChatFence();
    }

    private static void buildChatFence() {
        System.out.println("    ----------------------------------------");
    }

    private void printAddConfirmation(int index) {
        int size = todoList.size();
        System.out.println("     Got it. I've added this task:");
        System.out.printf("       %s%n", todoList.get(index).showTask());
        System.out.printf("     Now you have %d %s in the list%n", size, size > 1 ? "tasks" : "task");
    }

    /**
     * Split the input string into first word (command) and others
     *
     * @param input user input
     * @return an array, first element is command, second element is input
     */
    private String[] parseInput(String input) {
        return input.split("\\s+", 2);
    }

    private void addTodo(String todo) {
        todoList.add(new Todo(todo));
        printAddConfirmation(todoList.size() - 1);
    }

    private void addDeadline(String deadline) throws DeadlineInvalidUsageException {
        String[] parsedDeadline = deadline.split("\\s*/by\\s*", 2);

        if (parsedDeadline.length < 2) {
            throw new DeadlineInvalidUsageException("You should specify the deadline by using `/by`");
        }
        if (parsedDeadline[0].equals("")) {
            throw new DeadlineInvalidUsageException("Deadline description cannot be empty.");
        }
        if (parsedDeadline[1].equals("")) {
            throw new DeadlineInvalidUsageException("Deadline date cannot be empty.");
        }
        todoList.add(new Deadline(parsedDeadline[0], parsedDeadline[1]));
        printAddConfirmation(todoList.size() - 1);
    }

    private void addEvent(String event) throws EventInvalidUsageException {
        String[] parsedEvent = event.split("\\s*/at\\s*", 2);

        if (parsedEvent.length < 2) {
            throw new EventInvalidUsageException("You should specify a date by using `/at`");
        }
        if (parsedEvent[0].equals("")) {
            throw new EventInvalidUsageException("Event description cannot be empty.");
        }
        if (parsedEvent[1].equals("")) {
            throw new EventInvalidUsageException("Event date cannot be empty.");
        }
        todoList.add(new Event(parsedEvent[0], parsedEvent[1]));
        printAddConfirmation(todoList.size() - 1);
    }
}


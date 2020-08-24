package duke.command;

import duke.Storage;
import duke.TaskList;
import duke.Ui;
import exception.InvalidUsageException;

/**
 * Remove a task
 */
public class DeleteCommand extends Command {
    int taskNumber;

    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Removes a specific task from task list and save the changes to storage file
     *
     * @param taskList current task list
     * @param ui       text ui interface
     * @param storage  storage file
     */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) {
        // Check that the task number makes sense.
        if (taskNumber >= 0 && taskNumber < taskList.size()) {
            ui.print("Noted. I've removed this task: ");
            ui.print(taskList.remove(taskNumber).showTask());
            ui.print(String.format("Now you have %d %s in the list",
                    taskList.size(), taskList.size() > 1 ? "tasks" : "task"));
            storage.save(taskList);
        } else {
            ui.print("Sorry, I can't find it in your list!");
        }
    }
}

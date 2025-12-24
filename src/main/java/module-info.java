module javafx.journal {
	requires javafx.controls;
	requires javafx.fxml;
	requires transitive javafx.graphics;
	requires org.controlsfx.controls;
	requires java.prefs;

	opens ca.footeware.javafx.journal to javafx.fxml;
	opens ca.footeware.javafx.journal.controllers to javafx.fxml;

	exports ca.footeware.javafx.journal;
	exports ca.footeware.javafx.journal.controllers;
	exports ca.footeware.javafx.journal.model;
	exports ca.footeware.javafx.journal.exceptions;
}
package ch.makery.address.view

import ch.makery.address.model.Person
import ch.makery.address.MainApp
import scalafx.scene.control.{TableView, TableColumn, Label, Alert}
import scalafxml.core.macros.sfxml
import scalafx.beans.property.{StringProperty}
import ch.makery.address.util.DateUtil._
import scalafx.Includes._
import scalafx.event.ActionEvent

@sfxml
class PersonOverviewController(
    private val personTable: TableView[Person],
    private val firstNameColumn: TableColumn[Person, String],
    private val lastNameColumn: TableColumn[Person, String],
    private val firstNameLabel: Label,
    private val lastNameLabel: Label,
    private val streetLabel: Label,
    private val postalCodeLabel: Label,
    private val cityLabel: Label,
    private val birthdayLabel: Label
) {
  // initialize Table View display contents model
  personTable.items = MainApp.personData
  // initialize columns's cell values
  firstNameColumn.cellValueFactory = { _.value.firstName }
  lastNameColumn.cellValueFactory = { _.value.lastName }

  showPersonDetails(None);

  personTable
    .selectionModel()
    .selectedItem
    .onChange((_, _, newValue) => showPersonDetails(Some(newValue)))

  private def showPersonDetails(person: Option[Person]) = {
    person match {
      case Some(person) =>
        // Fill the labels with info from the person object.
        firstNameLabel.text <== person.firstName
        lastNameLabel.text <== person.lastName
        streetLabel.text <== person.street
        cityLabel.text <== person.city;
        postalCodeLabel.text = person.postalCode.value.toString
        birthdayLabel.text = person.date.value.asString
      case None =>
        // Person is null, remove all the text.
        firstNameLabel.text = ""
        lastNameLabel.text = ""
        streetLabel.text = ""
        postalCodeLabel.text = ""
        cityLabel.text = ""
        birthdayLabel.text = ""
    }
  }
  def handleNewPerson(action: ActionEvent) = {
    val person = new Person("", "")
    val okClicked = MainApp.showPersonEditDialog(person);
    if (okClicked) {
      MainApp.personData += person
      person.save()
    }
  }
  def handleEditPerson(action: ActionEvent) = {
    val selectedPerson = personTable.selectionModel().selectedItem.value
    if (selectedPerson != null) {
      val okClicked = MainApp.showPersonEditDialog(selectedPerson)

      if (okClicked) {
        showPersonDetails(Some(selectedPerson))
        selectedPerson.save()
      }

    } else {
      // Nothing selected.
      val alert = new Alert(Alert.AlertType.Warning) {
        initOwner(MainApp.stage)
        title = "No Selection"
        headerText = "No Person Selected"
        contentText = "Please select a person in the table."
      }.showAndWait()
    }
  }
  def handleDeletePerson(action: ActionEvent) = {
    val selectedIndex = personTable.selectionModel().selectedIndex.value
    if (selectedIndex >= 0) {
      personTable.items().remove(selectedIndex).delete();

    }
  }

}

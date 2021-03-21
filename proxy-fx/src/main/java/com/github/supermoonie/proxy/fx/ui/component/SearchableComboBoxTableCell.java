package com.github.supermoonie.proxy.fx.ui.component;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.SearchableComboBox;

import static com.github.supermoonie.proxy.fx.ui.component.CellUtils.createComboBox;

/**
 * @author supermoonie
 * @since 2020/10/28
 */
public class SearchableComboBoxTableCell<S,T> extends TableCell<S,T> {

    /***************************************************************************
     *                                                                         *
     * Static cell factories                                                   *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a ComboBox cell factory for use in {@link TableColumn} controls.
     * By default, the ComboBoxCell is rendered as a {@link Label} when not
     * being edited, and as a ComboBox when in editing mode. The ComboBox will,
     * by default, stretch to fill the entire list cell.
     *
     * @param <S> The type of the TableView generic type
     * @param <T> The type of the elements contained within the TableColumn.
     * @param items Zero or more items that will be shown to the user when the
     *      {@link ComboBox} menu is showing. These items must be of the same
     *      type as the TableColumn. Note that it is up to the developer to set
     *      {@link EventHandler event handlers} to listen to edit events in the
     *      TableColumn, and react accordingly. Methods of interest include
     *      {@link TableColumn#setOnEditStart(javafx.event.EventHandler) setOnEditStart},
     *      {@link TableColumn#setOnEditCommit(javafx.event.EventHandler) setOnEditCommit},
     *      and {@link TableColumn#setOnEditCancel(javafx.event.EventHandler) setOnEditCancel}.
     * @return A {@link Callback} that will return a TableCell that is able to
     *      work on the type of element contained within the TableColumn.
     */
    @SafeVarargs
    public static <S,T> Callback<TableColumn<S,T>, TableCell<S,T>> forTableColumn(
            final T... items) {
        return forTableColumn(null, items);
    }

    /**
     * Creates a ComboBox cell factory for use in {@link TableColumn} controls.
     * By default, the ComboBoxCell is rendered as a {@link Label} when not
     * being edited, and as a ComboBox when in editing mode. The ComboBox will,
     * by default, stretch to fill the entire list cell.
     *
     * @param <S> The type of the TableView generic type
     * @param <T> The type of the elements contained within the TableColumn.
     * @param converter A {@link StringConverter} to convert the given item (of
     *      type T) to a String for displaying to the user.
     * @param items Zero or more items that will be shown to the user when the
     *      {@link ComboBox} menu is showing. These items must be of the same
     *      type as the TableColumn. Note that it is up to the developer to set
     *      {@link EventHandler event handlers} to listen to edit events in the
     *      TableColumn, and react accordingly. Methods of interest include
     *      {@link TableColumn#setOnEditStart(javafx.event.EventHandler) setOnEditStart},
     *      {@link TableColumn#setOnEditCommit(javafx.event.EventHandler) setOnEditCommit},
     *      and {@link TableColumn#setOnEditCancel(javafx.event.EventHandler) setOnEditCancel}.
     * @return A {@link Callback} that will return a TableCell that is able to
     *      work on the type of element contained within the TableColumn.
     */
    @SafeVarargs
    public static <S,T> Callback<TableColumn<S,T>, TableCell<S,T>> forTableColumn(
            final StringConverter<T> converter,
            final T... items) {
        return forTableColumn(converter, FXCollections.observableArrayList(items));
    }

    /**
     * Creates a ComboBox cell factory for use in {@link TableColumn} controls.
     * By default, the ComboBoxCell is rendered as a {@link Label} when not
     * being edited, and as a ComboBox when in editing mode. The ComboBox will,
     * by default, stretch to fill the entire list cell.
     *
     * @param <S> The type of the TableView generic type
     * @param <T> The type of the elements contained within the TableColumn.
     * @param items Zero or more items that will be shown to the user when the
     *      {@link ComboBox} menu is showing. These items must be of the same
     *      type as the TableColumn. Note that it is up to the developer to set
     *      {@link EventHandler event handlers} to listen to edit events in the
     *      TableColumn, and react accordingly. Methods of interest include
     *      {@link TableColumn#setOnEditStart(javafx.event.EventHandler) setOnEditStart},
     *      {@link TableColumn#setOnEditCommit(javafx.event.EventHandler) setOnEditCommit},
     *      and {@link TableColumn#setOnEditCancel(javafx.event.EventHandler) setOnEditCancel}.
     * @return A {@link Callback} that will return a TableCell that is able to
     *      work on the type of element contained within the TableColumn.
     */
    public static <S,T> Callback<TableColumn<S,T>, TableCell<S,T>> forTableColumn(
            final ObservableList<T> items) {
        return forTableColumn(null, items);
    }

    /**
     * Creates a ComboBox cell factory for use in {@link TableColumn} controls.
     * By default, the ComboBoxCell is rendered as a {@link Label} when not
     * being edited, and as a ComboBox when in editing mode. The ComboBox will,
     * by default, stretch to fill the entire list cell.
     *
     * @param <S> The type of the TableView generic type
     * @param <T> The type of the elements contained within the TableColumn.
     * @param converter A {@link StringConverter} to convert the given item (of
     *      type T) to a String for displaying to the user.
     * @param items Zero or more items that will be shown to the user when the
     *      {@link ComboBox} menu is showing. These items must be of the same
     *      type as the TableColumn. Note that it is up to the developer to set
     *      {@link EventHandler event handlers} to listen to edit events in the
     *      TableColumn, and react accordingly. Methods of interest include
     *      {@link TableColumn#setOnEditStart(javafx.event.EventHandler) setOnEditStart},
     *      {@link TableColumn#setOnEditCommit(javafx.event.EventHandler) setOnEditCommit},
     *      and {@link TableColumn#setOnEditCancel(javafx.event.EventHandler) setOnEditCancel}.
     * @return A {@link Callback} that will return a TableCell that is able to
     *      work on the type of element contained within the TableColumn.
     */
    public static <S,T> Callback<TableColumn<S,T>, TableCell<S,T>> forTableColumn(
            final StringConverter<T> converter,
            final ObservableList<T> items) {
        return list -> new SearchableComboBoxTableCell<S,T>(converter, items);
    }



    /***************************************************************************
     *                                                                         *
     * Fields                                                                  *
     *                                                                         *
     **************************************************************************/

    private final ObservableList<T> items;

    private SearchableComboBox<T> comboBox;



    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a default ComboBoxTableCell with an empty items list.
     */
    public SearchableComboBoxTableCell() {
        this(FXCollections.<T>observableArrayList());
    }

    /**
     * Creates a default {@link ComboBoxTableCell} instance with the given items
     * being used to populate the {@link ComboBox} when it is shown.
     *
     * @param items The items to show in the ComboBox popup menu when selected
     *      by the user.
     */
    @SafeVarargs
    public SearchableComboBoxTableCell(T... items) {
        this(FXCollections.observableArrayList(items));
    }

    /**
     * Creates a {@link ComboBoxTableCell} instance with the given items
     * being used to populate the {@link ComboBox} when it is shown, and the
     * {@link StringConverter} being used to convert the item in to a
     * user-readable form.
     *
     * @param converter A {@link StringConverter} that can convert an item of type T
     *      into a user-readable string so that it may then be shown in the
     *      ComboBox popup menu.
     * @param items The items to show in the ComboBox popup menu when selected
     *      by the user.
     */
    @SafeVarargs
    public SearchableComboBoxTableCell(StringConverter<T> converter, T... items) {
        this(converter, FXCollections.observableArrayList(items));
    }

    /**
     * Creates a default {@link ComboBoxTableCell} instance with the given items
     * being used to populate the {@link ComboBox} when it is shown.
     *
     * @param items The items to show in the ComboBox popup menu when selected
     *      by the user.
     */
    public SearchableComboBoxTableCell(ObservableList<T> items) {
        this(null, items);
    }

    /**
     * Creates a {@link ComboBoxTableCell} instance with the given items
     * being used to populate the {@link ComboBox} when it is shown, and the
     * {@link StringConverter} being used to convert the item in to a
     * user-readable form.
     *
     * @param converter A {@link StringConverter} that can convert an item of type T
     *      into a user-readable string so that it may then be shown in the
     *      ComboBox popup menu.
     * @param items The items to show in the ComboBox popup menu when selected
     *      by the user.
     */
    public SearchableComboBoxTableCell(StringConverter<T> converter, ObservableList<T> items) {
        this.getStyleClass().add("combo-box-table-cell");
        this.items = items;
        setConverter(converter != null ? converter : CellUtils.<T>defaultStringConverter());
    }



    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/

    // --- converter
    private ObjectProperty<StringConverter<T>> converter =
            new SimpleObjectProperty<StringConverter<T>>(this, "converter");

    /**
     * The {@link StringConverter} property.
     * @return the {@link StringConverter} property
     */
    public final ObjectProperty<StringConverter<T>> converterProperty() {
        return converter;
    }

    /**
     * Sets the {@link StringConverter} to be used in this cell.
     * @param value the {@link StringConverter} to be used in this cell
     */
    public final void setConverter(StringConverter<T> value) {
        converterProperty().set(value);
    }

    /**
     * Returns the {@link StringConverter} used in this cell.
     * @return the {@link StringConverter} used in this cell
     */
    public final StringConverter<T> getConverter() {
        return converterProperty().get();
    }


    // --- comboBox editable
    private BooleanProperty comboBoxEditable =
            new SimpleBooleanProperty(this, "comboBoxEditable");

    /**
     * A property representing whether the ComboBox, when shown to the user,
     * is editable or not.
     * @return the property representing whether the ComboBox, when shown to the
     * user, is editable or not
     */
    public final BooleanProperty comboBoxEditableProperty() {
        return comboBoxEditable;
    }

    /**
     * Configures the ComboBox to be editable (to allow user input outside of the
     * options provide in the dropdown list).
     * @param value the editable value to be set for this ComboBox
     */
    public final void setComboBoxEditable(boolean value) {
        comboBoxEditableProperty().set(value);
    }

    /**
     * Returns true if the ComboBox is editable.
     * @return true if the ComboBox is editable
     */
    public final boolean isComboBoxEditable() {
        return comboBoxEditableProperty().get();
    }



    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * Returns the items to be displayed in the ComboBox when it is showing.
     * @return the items to be displayed in this ComboBox when it is showing
     */
    public ObservableList<T> getItems() {
        return items;
    }

    /** {@inheritDoc} */
    @Override public void startEdit() {
        if (! isEditable() || ! getTableView().isEditable() || ! getTableColumn().isEditable()) {
            return;
        }

        if (comboBox == null) {
            comboBox = createComboBox(this, items, converterProperty());
            comboBox.editableProperty().bind(comboBoxEditableProperty());
        }

        comboBox.getSelectionModel().select(getItem());

        super.startEdit();
        setText(null);
        setGraphic(comboBox);
    }

    /** {@inheritDoc} */
    @Override public void cancelEdit() {
        super.cancelEdit();

        setText(getConverter().toString(getItem()));
        setGraphic(null);
    }

    /** {@inheritDoc} */
    @Override public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        CellUtils.updateItem(this, getConverter(), null, null, comboBox);
    }
}

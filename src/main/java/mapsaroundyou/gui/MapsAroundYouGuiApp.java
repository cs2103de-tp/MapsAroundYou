package mapsaroundyou.gui;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import mapsaroundyou.app.ApplicationFactory;
import mapsaroundyou.common.DataLoadException;
import mapsaroundyou.common.InvalidInputException;
import mapsaroundyou.model.DatasetMetadata;
import mapsaroundyou.model.Destination;
import mapsaroundyou.model.ListingDetails;
import mapsaroundyou.model.SortMode;
import mapsaroundyou.model.UserPreferences;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class MapsAroundYouGuiApp extends Application {
    private static final int MIN_WIDTH = 1000;
    private static final int MIN_HEIGHT = 600;
    private static final int CONTROLS_PANEL_WIDTH = 340;
    private static final int RESULTS_TABLE_MIN_WIDTH = 560;
    private static final int LISTING_COLUMN_MIN_WIDTH = 210;
    private static final int RENT_COLUMN_MIN_WIDTH = 90;
    private static final int COMMUTE_COLUMN_MIN_WIDTH = 90;
    private static final int AIRCON_COLUMN_MIN_WIDTH = 70;
    private static final int SCORE_COLUMN_MIN_WIDTH = 80;
    private static final double LISTING_COLUMN_WIDTH_RATIO = 0.42d;
    private static final double RENT_COLUMN_WIDTH_RATIO = 0.15d;
    private static final double COMMUTE_COLUMN_WIDTH_RATIO = 0.15d;
    private static final double AIRCON_COLUMN_WIDTH_RATIO = 0.13d;
    private static final double SCORE_COLUMN_WIDTH_RATIO = 0.15d;
    private static final int DETAILS_PANEL_HEIGHT = 165;
    private static final int DETAILS_LABEL_WIDTH = 72;

    private GuiSearchService searchService;

    private final ComboBox<Destination> destinationComboBox = new ComboBox<>();
    private final TextField maxRentField = new TextField();
    private final TextField maxCommuteField = new TextField();
    private final TextField maxWalkField = new TextField();
    private final CheckBox requireAirconCheckBox = new CheckBox("Require aircon");
    private final TextField resultLimitField = new TextField();
    private final CheckBox excludeWalkDominantRoutesCheckBox = new CheckBox("No walk-dominant routes");
    private final Button searchButton = new Button("Search");
    private final VBox maxWalkGroup = new VBox();

    private final TableView<SearchRow> resultsTable = new TableView<>();
    private final TableColumn<SearchRow, String> listingColumn = new TableColumn<>("Listing");
    private final TableColumn<SearchRow, Number> rentColumn = new TableColumn<>("Rent (SGD)");
    private final TableColumn<SearchRow, Number> commuteColumn = new TableColumn<>("Commute");
    private final TableColumn<SearchRow, Boolean> airconColumn = new TableColumn<>("A/C");
    private final TableColumn<SearchRow, Number> matchColumn = new TableColumn<>("Match");
    private final Label statusLabel = new Label("App status: Ready.");
    private final ProgressIndicator loadingIndicator = new ProgressIndicator();
    private final Label datasetLabel = new Label();

    private final Label detailsTitle = new Label("-");
    private final Label detailsAddress = new Label("-");
    private final Label detailsRoomType = new Label("-");
    private final Label detailsRent = new Label("-");
    private final Label detailsAircon = new Label("-");
    private final Label detailsCommute = new Label("-");
    private final Label detailsScore = new Label("-");
    private final Label detailsSource = new Label("-");
    private final Label detailsNotes = new Label("-");

    @Override
    public void start(Stage stage) {
        try {
            this.searchService = new GuiSearchService(ApplicationFactory.createSearchLogic());
        } catch (DataLoadException exception) {
            showFatalStartupError(stage, exception.getMessage());
            return;
        }

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));
        root.setTop(buildHeader());
        root.setLeft(buildControls());
        root.setCenter(buildContentArea());

        Scene scene = new Scene(root, MIN_WIDTH, MIN_HEIGHT);
        stage.setTitle("MapsAroundYou");
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        stage.setScene(scene);
        stage.show();

        configureInteractions();
        loadInitialData();
    }

    private VBox buildHeader() {
        Label title = new Label("MapsAroundYou");
        title.setFont(Font.font(20));

        datasetLabel.setWrapText(true);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topRow = new HBox(12, title, spacer, buildStatusIndicator());
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox header = new VBox(4, topRow, datasetLabel);
        header.setPadding(new Insets(0, 0, 8, 0));
        return header;
    }

    private VBox buildControls() {
        destinationComboBox.setTooltip(new Tooltip("Choose a supported destination"));
        destinationComboBox.setMaxWidth(Double.MAX_VALUE);

        maxRentField.setPromptText("e.g. 1800");
        maxRentField.setMaxWidth(Double.MAX_VALUE);
        maxCommuteField.setPromptText("e.g. 45");
        maxCommuteField.setMaxWidth(Double.MAX_VALUE);
        maxWalkField.setPromptText("e.g. 10");
        maxWalkField.setMaxWidth(Double.MAX_VALUE);
        resultLimitField.setPromptText("e.g. 10");
        resultLimitField.setMaxWidth(Double.MAX_VALUE);

        maxWalkGroup.getChildren().setAll(createControlGroup("Max walking time (minutes)", maxWalkField));
        maxWalkGroup.visibleProperty().bind(excludeWalkDominantRoutesCheckBox.selectedProperty());
        maxWalkGroup.managedProperty().bind(maxWalkGroup.visibleProperty());

        VBox walkingPreferenceGroup = new VBox(6, excludeWalkDominantRoutesCheckBox, maxWalkGroup);
        walkingPreferenceGroup.setFillWidth(true);

        VBox form = new VBox(
                10,
                createControlGroup("Destination", destinationComboBox),
                createControlGroup("Max rent (SGD)", maxRentField),
                createControlGroup("Max commute (minutes)", maxCommuteField),
                createControlGroup("Aircon", requireAirconCheckBox),
                createControlGroup("Result limit", resultLimitField),
                createControlGroup("Walking preference", walkingPreferenceGroup)
        );

        searchButton.setDefaultButton(true);
        searchButton.setMaxWidth(Double.MAX_VALUE);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox box = new VBox(10, form, spacer, searchButton);
        box.setPadding(new Insets(10, 30, 10, 18));
        box.setPrefWidth(CONTROLS_PANEL_WIDTH);
        box.setMinWidth(CONTROLS_PANEL_WIDTH);
        box.setFillWidth(true);
        return box;
    }

    private static VBox createControlGroup(String labelText, Node content) {
        Label label = new Label(labelText);
        if (content instanceof Region region) {
            region.setMaxWidth(Double.MAX_VALUE);
        }
        VBox group = new VBox(4, label, content);
        group.setFillWidth(true);
        return group;
    }

    private VBox buildResultsTable() {
        resultsTable.setPlaceholder(new Label("No results yet. Set filters and click Search."));
        resultsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        resultsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        resultsTable.setMinWidth(RESULTS_TABLE_MIN_WIDTH);
        configureTableColumns();
        resultsTable.getColumns().setAll(List.of(
                listingColumn,
                rentColumn,
                commuteColumn,
                airconColumn,
                matchColumn
        ));

        VBox box = new VBox(resultsTable);
        VBox.setVgrow(resultsTable, Priority.ALWAYS);
        box.setPadding(new Insets(0, 12, 0, 0));
        return box;
    }

    private VBox buildContentArea() {
        VBox content = new VBox(12, buildResultsTable(), buildDetailsPanel());
        VBox.setVgrow(content.getChildren().get(0), Priority.ALWAYS);
        return content;
    }

    private VBox buildDetailsPanel() {
        Label header = new Label("Selected Listing");
        header.setFont(Font.font(16));
        detailsTitle.setFont(Font.font(15));

        detailsTitle.setWrapText(true);
        detailsAddress.setWrapText(true);
        detailsCommute.setWrapText(true);
        detailsSource.setWrapText(true);
        detailsNotes.setWrapText(true);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(6);
        grid.setPadding(new Insets(6, 0, 0, 0));
        grid.getColumnConstraints().addAll(
                createDetailsLabelColumn(),
                createDetailsValueColumn(),
                createDetailsLabelColumn(),
                createDetailsValueColumn()
        );

        int row = 0;
        addDetailRow(grid, row++, "Address", detailsAddress, "Room type", detailsRoomType);
        addDetailRow(grid, row++, "Rent", detailsRent, "Aircon", detailsAircon);
        addDetailRow(grid, row++, "Commute", detailsCommute, "Match", detailsScore);
        addDetailRow(grid, row++, "Source", detailsSource, "Notes", detailsNotes);

        VBox box = new VBox(6, header, detailsTitle, grid);
        box.setPrefHeight(DETAILS_PANEL_HEIGHT);
        box.setMinHeight(DETAILS_PANEL_HEIGHT);
        box.setFillWidth(true);
        return box;
    }

    private static ColumnConstraints createDetailsLabelColumn() {
        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setMinWidth(DETAILS_LABEL_WIDTH);
        labelColumn.setPrefWidth(DETAILS_LABEL_WIDTH);
        return labelColumn;
    }

    private static ColumnConstraints createDetailsValueColumn() {
        ColumnConstraints valueColumn = new ColumnConstraints();
        valueColumn.setHgrow(Priority.ALWAYS);
        valueColumn.setFillWidth(true);
        return valueColumn;
    }

    private static void addDetailRow(
            GridPane grid,
            int row,
            String leftLabel,
            Label leftValue,
            String rightLabel,
            Label rightValue
    ) {
        grid.add(new Label(leftLabel), 0, row);
        grid.add(leftValue, 1, row);
        grid.add(new Label(rightLabel), 2, row);
        grid.add(rightValue, 3, row);
    }

    private HBox buildStatusIndicator() {
        loadingIndicator.setVisible(false);
        loadingIndicator.setPrefSize(18, 18);

        statusLabel.setWrapText(false);
        HBox bar = new HBox(10, statusLabel, loadingIndicator);
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    private void configureTableColumns() {
        listingColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getTitle()));
        listingColumn.setMinWidth(LISTING_COLUMN_MIN_WIDTH);
        listingColumn.prefWidthProperty().bind(resultsTable.widthProperty().multiply(LISTING_COLUMN_WIDTH_RATIO));
        listingColumn.setSortable(false);
        listingColumn.setCellFactory(createCenteredCellFactory(Function.identity()));

        rentColumn.setCellValueFactory(
                cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getMonthlyRent())
        );
        rentColumn.setMinWidth(RENT_COLUMN_MIN_WIDTH);
        rentColumn.prefWidthProperty().bind(resultsTable.widthProperty().multiply(RENT_COLUMN_WIDTH_RATIO));
        rentColumn.setCellFactory(createCenteredCellFactory(value -> Integer.toString(value.intValue())));

        commuteColumn.setCellValueFactory(
                cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getTotalCommuteMinutes())
        );
        commuteColumn.setMinWidth(COMMUTE_COLUMN_MIN_WIDTH);
        commuteColumn.prefWidthProperty().bind(resultsTable.widthProperty().multiply(COMMUTE_COLUMN_WIDTH_RATIO));
        commuteColumn.setCellFactory(createCenteredCellFactory(value -> Integer.toString(value.intValue())));

        airconColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().hasAircon()));
        airconColumn.setMinWidth(AIRCON_COLUMN_MIN_WIDTH);
        airconColumn.prefWidthProperty().bind(resultsTable.widthProperty().multiply(AIRCON_COLUMN_WIDTH_RATIO));
        airconColumn.setCellFactory(createCenteredCellFactory(item -> item ? "Yes" : "No"));

        matchColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getScore()));
        matchColumn.setMinWidth(SCORE_COLUMN_MIN_WIDTH);
        matchColumn.prefWidthProperty().bind(resultsTable.widthProperty().multiply(SCORE_COLUMN_WIDTH_RATIO));
        matchColumn.setCellFactory(createCenteredCellFactory(item -> String.format("%.3f", item.doubleValue())));
    }

    private void configureInteractions() {
        destinationComboBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Destination destination) {
                return GuiTextFormatter.formatDestination(destination);
            }

            @Override
            public Destination fromString(String string) {
                return null;
            }
        });

        searchButton.setOnAction(event -> runSearch());

        resultsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                clearDetails();
                return;
            }
            populateDetails(newVal);
        });
    }

    private static <T> javafx.util.Callback<TableColumn<SearchRow, T>, TableCell<SearchRow, T>>
            createCenteredCellFactory(
            Function<T, String> formatter
    ) {
        return column -> new TableCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER);
                if (empty || item == null) {
                    setText(null);
                    return;
                }
                setText(formatter.apply(item));
            }
        };
    }

    private void loadInitialData() {
        setBusy(true, "Loading dataset...");

        Task<InitialData> initTask = new Task<>() {
            @Override
            protected InitialData call() {
                return new InitialData(
                        searchService.getSupportedDestinations(),
                        searchService.getDatasetMetadata(),
                        searchService.getCurrentPreferences()
                );
            }
        };

        initTask.setOnSucceeded(e -> {
            InitialData initialData = initTask.getValue();
            destinationComboBox.setItems(FXCollections.observableArrayList(initialData.destinations()));
            if (!initialData.destinations().isEmpty()) {
                destinationComboBox.getSelectionModel().select(
                        resolveInitialDestination(initialData.destinations(), initialData.preferences())
                );
            }
            applyInitialPreferences(initialData.preferences());
            datasetLabel.setText(GuiTextFormatter.formatDatasetMetadata(initialData.metadata()));
            setBusy(false, "Ready.");
        });
        initTask.setOnFailed(e -> {
            setBusy(false, "Failed to load dataset: " + GuiErrorTranslator.toUserMessage(initTask.getException()));
        });

        GuiTaskRunner.run(initTask, "mapsaroundyou-init");
    }

    private void runSearch() {
        Destination destination = destinationComboBox.getValue();
        if (destination == null) {
            setBusy(false, "Please choose a destination.");
            return;
        }

        SearchRequest request;
        try {
            request = GuiSearchRequestParser.parse(
                    destination,
                    maxRentField.getText(),
                    maxCommuteField.getText(),
                    maxWalkField.getText(),
                    requireAirconCheckBox.isSelected(),
                    resultLimitField.getText(),
                    currentTableSortMode(),
                    excludeWalkDominantRoutesCheckBox.isSelected()
            );
        } catch (InvalidInputException exception) {
            setBusy(false, exception.getMessage());
            return;
        }

        setBusy(true, "Searching...");
        resultsTable.getItems().clear();
        clearDetails();

        Task<SearchResponse> task = new Task<>() {
            @Override
            protected SearchResponse call() {
                return searchService.search(request);
            }
        };

        task.setOnSucceeded(e -> {
            SearchResponse response = task.getValue();
            datasetLabel.setText(GuiTextFormatter.formatDatasetMetadata(response.datasetMetadata()));
            resultsTable.setItems(FXCollections.observableArrayList(
                    response.results().stream().map(SearchRow::new).toList()
            ));
            resultsTable.sort();
            if (!resultsTable.getItems().isEmpty()) {
                resultsTable.getSelectionModel().select(0);
            }
            setBusy(false, "Found " + response.results().size() + " result(s).");
        });

        task.setOnFailed(e -> {
            setBusy(false, GuiErrorTranslator.toUserMessage(task.getException()));
        });

        GuiTaskRunner.run(task, "mapsaroundyou-search");
    }

    private void populateDetails(SearchRow row) {
        Objects.requireNonNull(row, "row");

        ListingDetails details;
        try {
            details = searchService.getListingDetails(row.getListingId());
        } catch (RuntimeException exception) {
            setStatus("Failed to load details: " + GuiErrorTranslator.toUserMessage(exception));
            return;
        }

        detailsTitle.setText(details.listing().title());
        detailsAddress.setText(details.listing().address());
        detailsRoomType.setText(details.listing().roomType());
        detailsRent.setText("SGD " + details.listing().monthlyRent());
        detailsAircon.setText(details.listing().hasAircon() ? "Yes" : "No");
        detailsScore.setText(String.format("%.3f", row.getScore()));
        detailsSource.setText(details.listing().sourcePlatform());
        detailsNotes.setText(GuiTextFormatter.formatOptionalText(details.listing().notes()));
        detailsCommute.setText(GuiTextFormatter.formatCommute(row.commute()));
    }

    private void clearDetails() {
        detailsTitle.setText("-");
        detailsAddress.setText("-");
        detailsRoomType.setText("-");
        detailsRent.setText("-");
        detailsAircon.setText("-");
        detailsCommute.setText("-");
        detailsScore.setText("-");
        detailsSource.setText("-");
        detailsNotes.setText("-");
    }

    private void setBusy(boolean busy, String message) {
        loadingIndicator.setVisible(busy);
        destinationComboBox.setDisable(busy);
        maxRentField.setDisable(busy);
        maxCommuteField.setDisable(busy);
        maxWalkField.setDisable(busy);
        requireAirconCheckBox.setDisable(busy);
        resultLimitField.setDisable(busy);
        excludeWalkDominantRoutesCheckBox.setDisable(busy);
        searchButton.setDisable(busy);
        resultsTable.setDisable(busy);
        setStatus(message);
    }

    private void setStatus(String message) {
        if (message == null || message.isBlank()) {
            statusLabel.setText("App status: ");
            return;
        }
        statusLabel.setText("App status: " + message);
    }

    static Destination resolveInitialDestination(List<Destination> destinations, UserPreferences preferences) {
        if (destinations.isEmpty()) {
            return null;
        }
        String preferredDestinationId = preferences == null ? null : preferences.destinationId();
        if (preferredDestinationId != null) {
            for (Destination destination : destinations) {
                if (destination.destinationId().equals(preferredDestinationId)) {
                    return destination;
                }
            }
        }
        return destinations.get(0);
    }

    private void applyInitialPreferences(UserPreferences preferences) {
        UserPreferences resolvedPreferences = preferences == null ? UserPreferences.defaults() : preferences;
        maxRentField.setText(Integer.toString(resolvedPreferences.maxRent()));
        maxCommuteField.setText(Integer.toString(Math.max(1, resolvedPreferences.maxCommuteMinutes())));
        maxWalkField.setText(Integer.toString(resolvedPreferences.maxWalkMinutes()));
        requireAirconCheckBox.setSelected(resolvedPreferences.requireAircon());
        resultLimitField.setText(Integer.toString(resolvedPreferences.resultLimit()));
        excludeWalkDominantRoutesCheckBox.setSelected(resolvedPreferences.excludeWalkDominantRoutes());
        applyTableSort(resolvedPreferences.sortMode());
    }

    private void applyTableSort(SortMode sortMode) {
        SortMode resolvedSortMode = sortMode == null ? SortMode.BALANCED : sortMode;
        switch (resolvedSortMode) {
        case RENT -> {
            rentColumn.setSortType(TableColumn.SortType.ASCENDING);
            resultsTable.getSortOrder().setAll(List.of(rentColumn));
        }
        case COMMUTE -> {
            commuteColumn.setSortType(TableColumn.SortType.ASCENDING);
            resultsTable.getSortOrder().setAll(List.of(commuteColumn));
        }
        case BALANCED -> {
            matchColumn.setSortType(TableColumn.SortType.DESCENDING);
            resultsTable.getSortOrder().setAll(List.of(matchColumn));
        }
        default -> throw new IllegalStateException("Unsupported sort mode: " + resolvedSortMode);
        }
    }

    private SortMode currentTableSortMode() {
        if (resultsTable.getSortOrder().isEmpty()) {
            return SortMode.BALANCED;
        }

        TableColumn<SearchRow, ?> primaryColumn = resultsTable.getSortOrder().getFirst();
        if (primaryColumn == rentColumn) {
            return SortMode.RENT;
        }
        if (primaryColumn == commuteColumn) {
            return SortMode.COMMUTE;
        }
        return SortMode.BALANCED;
    }

    private record InitialData(List<Destination> destinations, DatasetMetadata metadata, UserPreferences preferences) {
    }

    private static void showFatalStartupError(Stage stage, String message) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(18));
        Label title = new Label("MapsAroundYou");
        title.setFont(Font.font(20));
        Label body = new Label("Startup error: " + (message == null ? "Unknown error" : message));
        body.setWrapText(true);
        VBox content = new VBox(10, title, body);
        root.setCenter(content);
        Scene scene = new Scene(root, 700, 200);
        stage.setTitle("MapsAroundYou");
        stage.setScene(scene);
        stage.show();
    }
}


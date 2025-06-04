package proxxy.lesothoTour;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import netscape.javascript.JSObject;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class HelloApplication extends Application {
    private MediaPlayer mediaPlayer;
    private Label quizLabel;
    private Label resultLabel;
    private Label instructions;
    private VBox quizBox;
    private MediaView mediaView;
    private Label locationTitle;
    private Label locationDescription;
    private VBox mediaContainer;
    private VBox infoContainer;
    private String currentLocationId;
    private String currentTitle;
    private String currentDescription;
    private HostServices hostServices;
    private StackPane contentPane;
    private HBox buttonContainer;
    private WebView webView;
    private ProgressIndicator mediaLoadingIndicator;
    private StackPane rootContainer;
    private BorderPane mainAppContent;

    // Quiz related variables
    private List<QuizQuestion> currentQuizQuestions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private Label scoreLabel;
    private Button nextQuestionButton;
    private Button submitAnswerButton;
    private Button restartQuizButton;
    private Button backButton;

    // Maps for storing URLs and descriptions
    private final Map<String, String> locationInfoUrls = new HashMap<>();
    private final Map<String, String> locationMediaUrls = new HashMap<>();
    private final Map<String, String> locationDescriptions = new HashMap<>();

    @Override
    public void start(Stage primaryStage) {
        // Initialize URL maps and descriptions
        initializeLocationUrlsAndDescriptions();

        hostServices = getHostServices();
        primaryStage.setTitle("Lesotho Virtual Tour Guide");

        // Create the root container that will hold either splash or main content
        rootContainer = new StackPane();

        // Create the splash screen
        createSplashScreen();

        // Create the main app content (but don't show it yet)
        createMainAppContent();

        // Initially show the splash screen
        rootContainer.getChildren().add(createSplashScreen());

        // Mobile-friendly scene size
        Scene scene = new Scene(rootContainer, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createMainAppContent() {
        // Main layout with mobile-friendly design
        mainAppContent = new BorderPane();
        mainAppContent.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10;");

        // Header with app title
        Label titleLabel = new Label("Lesotho Explorer");
        titleLabel.setFont(Font.font(22));
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
        HBox header = new HBox(titleLabel);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: #2c3e50; -fx-padding: 15;");
        mainAppContent.setTop(header);

        // Main content area
        setupMainContent(mainAppContent);
    }

    private StackPane createSplashScreen() {
        StackPane splashPane = new StackPane();

        // Load background image (ensure it's in the correct path in resources)
        Image backgroundImage = new Image(getClass().getResource("/images/background.jpg").toExternalForm());

        // Create an ImageView and set it to fill the pane
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setFitWidth(800); // adjust based on your window size
        backgroundImageView.setFitHeight(600);
        backgroundImageView.setPreserveRatio(false); // fill entire area
        backgroundImageView.setSmooth(true);

        // Bind the ImageView to always fill the splashPane size
        backgroundImageView.fitWidthProperty().bind(splashPane.widthProperty());
        backgroundImageView.fitHeightProperty().bind(splashPane.heightProperty());

        // Semi-transparent overlay
        Pane overlay = new Pane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);");
        overlay.prefWidthProperty().bind(splashPane.widthProperty());
        overlay.prefHeightProperty().bind(splashPane.heightProperty());

        // Content container
        VBox splashContent = new VBox(30);
        splashContent.setAlignment(Pos.CENTER);

        Label splashTitle = new Label("Lesotho Virtual Tour");
        splashTitle.setFont(Font.font(36));
        splashTitle.setTextFill(Color.WHITE);
        splashTitle.setStyle("-fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 10, 0, 0, 1);");

        Button startButton = new Button("Start Tour");
        startButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 18px; "
                + "-fx-padding: 12 30; -fx-background-radius: 25; -fx-cursor: hand;");
        startButton.setOnMouseEntered(e -> startButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; "
                + "-fx-font-size: 18px; -fx-padding: 12 30; -fx-background-radius: 25; -fx-cursor: hand;"));
        startButton.setOnMouseExited(e -> startButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; "
                + "-fx-font-size: 18px; -fx-padding: 12 30; -fx-background-radius: 25; -fx-cursor: hand;"));

        startButton.setOnAction(e -> {
            rootContainer.getChildren().clear();
            rootContainer.getChildren().add(mainAppContent);
        });

        splashContent.getChildren().addAll(splashTitle, startButton);

        // Add everything in the correct order: background → overlay → content
        splashPane.getChildren().addAll(backgroundImageView, overlay, splashContent);

        return splashPane;
    }

    private void initializeLocationUrlsAndDescriptions() {
        // Information URLs
        locationInfoUrls.put("Maseru", "https://en.wikipedia.org/wiki/Maseru");
        locationDescriptions.put("Maseru", "Capital city of Lesotho<br>Founded in 1869<br>Elevation: 1,600m<br>Main economic hub<br>Home to government buildings<br>Cultural center");
        locationInfoUrls.put("Teyateyaneng", "https://en.wikipedia.org/wiki/Teyateyaneng");
        locationDescriptions.put("Teyateyaneng", "Known as 'TY'<br>Center for traditional crafts<br>Home to Basotho hat makers<br>Textile industry<br>Agricultural market town");
        locationInfoUrls.put("Butha-Buthe", "https://en.wikipedia.org/wiki/Butha-Buthe");
        locationDescriptions.put("Butha-Buthe", "Northern city<br>Gateway to Afriski<br>Historic town<br>Surrounded by mountains<br>Important transportation hub");
        locationInfoUrls.put("Leribe", "https://en.wikipedia.org/wiki/Hlotse");
        locationDescriptions.put("Leribe", "Agricultural center<br>Home to Maliba Lodge<br>Important market town<br>Fertile farmlands<br>Historical significance");
        locationInfoUrls.put("Pioneer-Mall", "https://www.pioneermall.co.ls/");
        locationDescriptions.put("Pioneer-Mall", "Largest shopping mall in Lesotho<br>Opened in 2010<br>Over 70 stores<br>Food court<br>Entertainment options<br>Modern architecture");
        locationInfoUrls.put("Mafeteng-Mall", "https://www.lesothotourism.org/listing/mafeteng-mall/");
        locationDescriptions.put("Mafeteng-Mall", "Modern shopping center<br>Variety of stores<br>Popular food court<br>Local and international brands<br>Community gathering space");
        locationInfoUrls.put("Maqalika-Dam-and-Lake", "https://www.lesothotourism.org/listing/maqalika-dam/");
        locationDescriptions.put("Maqalika-Dam-and-Lake", "Scenic water reservoir<br>Recreational activities<br>Beautiful mountain views<br>Picnic spots<br>Bird watching area<br>Popular weekend destination");
        locationInfoUrls.put("Maletsunyane", "https://en.wikipedia.org/wiki/Maletsunyane_Falls");
        locationDescriptions.put("Maletsunyane", "192m high waterfall<br>One of highest single-drop falls in Africa<br>Popular abseiling site<br>Hiking trails<br>Spectacular views<br>Natural wonder");
        locationInfoUrls.put("Lion-Rock", "https://www.lesothotourism.org/listing/lions-rock/");
        locationDescriptions.put("Lion-Rock", "Iconic mountain formation<br>Resembles a lion's head<br>Popular hiking destination<br>Geological wonder<br>Panoramic views<br>Photography hotspot");
        locationInfoUrls.put("Lancers-Gap", "https://www.lesothotourism.org/listing/lancers-gap/");
        locationDescriptions.put("Lancers-Gap", "Scenic mountain pass<br>Stunning views of Maseru<br>Popular hiking destination<br>Historical significance<br>Military history<br>Nature trails");
        locationInfoUrls.put("Afriski", "https://www.afriski.net/");
        locationDescriptions.put("Afriski", "Only ski resort in Lesotho<br>Winter sports destination<br>Mountain lodge<br>Year-round activities<br>Highest pub in Africa<br>Adventure tourism");
        locationInfoUrls.put("Thaba-Bosiu", "https://en.wikipedia.org/wiki/Thaba_Bosiu");
        locationDescriptions.put("Thaba-Bosiu", "Mountain fortress of King Moshoeshoe I<br>Founded in 1824<br>UNESCO World Heritage Site<br>Birthplace of Basotho nation<br>Historical tours<br>Cultural significance");
        locationInfoUrls.put("Morija-Museum", "https://www.morijamuseum.org/");
        locationDescriptions.put("Morija-Museum", "Oldest museum in Lesotho<br>Cultural heritage center<br>Annual arts festival<br>Historical artifacts<br>Educational programs<br>Research facility");
        locationInfoUrls.put("Ha-Kome", "https://www.lesothotourism.org/listing/ha-kome-cave-dwellings/");
        locationDescriptions.put("Ha-Kome", "Ancient cave dwellings<br>Traditional architecture<br>UNESCO tentative list<br>Living museum<br>Cultural heritage site<br>Unique accommodation");
        locationInfoUrls.put("Katse-Dam", "https://en.wikipedia.org/wiki/Katse_Dam");
        locationDescriptions.put("Katse-Dam", "Part of Lesotho Highlands Water Project<br>710m long, 185m high<br>Provides water to South Africa<br>Engineering marvel<br>Tourist attraction<br>Boat tours available");
        locationInfoUrls.put("Mohale-Dam", "https://en.wikipedia.org/wiki/Mohale_Dam");
        locationDescriptions.put("Mohale-Dam", "Second largest dam in Lesotho<br>Part of LHWP<br>Beautiful mountain setting<br>Scenic drives<br>Water sports<br>Environmental importance");

        // Updated Media URLs with actual relevant short videos for each location
        locationMediaUrls.put("Maseru", "https://www.youtube.com/shorts/G0LYuPFB-Hc"); // Maseru City Tour
        locationMediaUrls.put("Teyateyaneng", "https://www.youtube.com/shorts/LbAKJgR02bo"); // TY Craft Village
        locationMediaUrls.put("Butha-Buthe", "https://www.youtube.com/shorts/48dV_EzcXjE"); // Butha-Buthe overview
        locationMediaUrls.put("Leribe", "https://www.youtube.com/shorts/yNeSZ0JAwOA"); // Leribe agricultural area
        locationMediaUrls.put("Pioneer-Mall", "https://www.youtube.com/watch?v=9xPRMXPhs_4"); // Pioneer Mall tour
        locationMediaUrls.put("Mafeteng-Mall", "https://www.youtube.com/watch?v=T0MoUTjj05E"); // Mafeteng Mall
        locationMediaUrls.put("Maqalika-Dam-and-Lake", "https://www.youtube.com/shorts/uMYYeeAo3zo"); // Maqalika Dam
        locationMediaUrls.put("Maletsunyane", "https://www.youtube.com/shorts/bm5goH_5JoA"); // Maletsunyane Falls
        locationMediaUrls.put("Lion-Rock", "https://www.youtube.com/watch?v=5vJvtSfx8eg"); // Lion Rock hike
        locationMediaUrls.put("Lancers-Gap", "https://www.tiktok.com/@matsoso_fpv/video/7456119783377931526"); // Lancers Gap view
        locationMediaUrls.put("Afriski", "https://www.youtube.com/shorts/UvZtrQea2ac"); // Afriski resort
        locationMediaUrls.put("Thaba-Bosiu", "https://www.youtube.com/shorts/dPZhfxTHeb8"); // Thaba Bosiu history
        locationMediaUrls.put("Morija-Museum", "https://www.youtube.com/shorts/-njQ1n7Se2w"); // Morija Museum
        locationMediaUrls.put("Ha-Kome", "https://www.youtube.com/watch?v=PVN47ByEitw"); // Ha Kome caves
        locationMediaUrls.put("Katse-Dam", "https://www.youtube.com/shorts/sNEyWRagREw"); // Katse Dam engineering
        locationMediaUrls.put("Mohale-Dam", "https://www.youtube.com/watch?v=gti0VG6D5CQ"); // Mohale Dam
    }

    private void setupMainContent(BorderPane root) {
        // Create split pane to divide map and content
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.7);

        // Create the WebView with Leaflet map
        webView = new WebView();
        webView.setPrefSize(800, 700);
        webView.getEngine().setJavaScriptEnabled(true);

        // HTML + Leaflet Map with all locations
        String htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <title>Lesotho Map</title>
            <link rel="stylesheet"
                          href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"
                          crossorigin=""/>
            <style>
                html, body, #map {
                    height: 100%;
                    width: 100%;
                    margin: 0;
                    padding: 0;
                    overflow: hidden;
                }
                .custom-popup .leaflet-popup-content-wrapper {
                    border-radius: 8px;
                    padding: 5px;
                }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"
                            crossorigin=""></script>
            <script>
                function debug(message) {
                    console.log(message);
                    if (window.javaConnector) {
                        window.javaConnector.log(message);
                    }
                }

                debug("Initializing map...");
                var map = L.map('map').setView([-29.61, 28.23], 7);

                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 19,
                    attribution: '© OpenStreetMap'
                }).addTo(map);

                var customIcon = L.icon({
                    iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon.png',
                    iconSize: [25, 41],
                    iconAnchor: [12, 41],
                    popupAnchor: [1, -34]
                });

                function addMarker(lat, lng, title, locationId, description) {
                    var marker = L.marker([lat, lng], {icon: customIcon})
                        .addTo(map)
                        .bindPopup('<div class="custom-popup"><b>' + title + '</b><br>' + description + '</div>');
                    
                    marker.on('click', function() {
                        debug("Clicked: " + locationId);
                        if (window.javaConnector && window.javaConnector.showLocation) {
                            window.javaConnector.showLocation(locationId, title, description);
                        }
                    });
                    return marker;
                }

                debug("Adding markers...");
                // Major Cities
                addMarker(-29.31, 27.48, "Maseru", "Maseru",
                    "Capital city of Lesotho");
                
                addMarker(-29.15, 27.75, "Teyateyaneng", "Teyateyaneng",
                    "Known as 'TY'");
                
                addMarker(-28.87, 28.05, "Butha-Buthe", "Butha-Buthe",
                    "Northern city");
                
                addMarker(-29.23, 27.95, "Leribe", "Leribe",
                    "Agricultural center");
                
                // Malls and Shopping Centers
                addMarker(-29.32, 27.50, "Pioneer Mall", "Pioneer-Mall",
                    "Largest shopping mall in Lesotho");
                
                addMarker(-29.30, 27.48, "Mafeteng Mall", "Mafeteng-Mall",
                    "Modern shopping center");
                
                addMarker(-29.31, 27.49, "Maqalika Dam and Lake", "Maqalika-Dam-and-Lake",
                    "Scenic water reservoir");
                
                // Natural Attractions
                addMarker(-29.90, 28.10, "Maletsunyane Falls", "Maletsunyane",
                    "192m high waterfall");
                
                addMarker(-29.33, 27.60, "Lion Rock Mountain", "Lion-Rock",
                    "Iconic mountain formation");
                
                addMarker(-29.45, 27.55, "Lancer's Gap", "Lancers-Gap",
                    "Scenic mountain pass");
                
                addMarker(-28.95, 28.70, "Afriski Mountain Resort", "Afriski",
                    "Only ski resort in Lesotho");
                
                // Historical and Cultural Sites
                addMarker(-29.36, 27.70, "Thaba Bosiu", "Thaba-Bosiu",
                    "Mountain fortress of King Moshoeshoe I");
                
                addMarker(-29.62, 27.48, "Morija Museum", "Morija-Museum",
                    "Oldest museum in Lesotho");
                
                addMarker(-29.40, 27.70, "Ha Kome Cave Houses", "Ha-Kome",
                    "Ancient cave dwellings");
                
                // Dams and Engineering Projects
                addMarker(-29.33, 28.50, "Katse Dam", "Katse-Dam",
                    "Part of Lesotho Highlands Water Project");
                
                addMarker(-29.45, 28.10, "Mohale Dam", "Mohale-Dam",
                    "Second largest dam in Lesotho");

                debug("Map initialization complete");
            </script>
        </body>
        </html>
        """;

        // Handle load errors
        webView.getEngine().getLoadWorker().exceptionProperty().addListener(
                (obs, oldExc, newExc) -> {
                    if (newExc != null) {
                        System.err.println("WebView Error: " + newExc.getMessage());
                        showError("Failed to load map. Please check your internet connection.");
                    }
                });

        // JavaScript-Java Bridge
        webView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                try {
                    JSObject window = (JSObject) webView.getEngine().executeScript("window");
                    window.setMember("javaConnector", new JavaConnector());
                    System.out.println("JavaScript bridge established");
                } catch (Exception e) {
                    System.err.println("Bridge error: " + e.getMessage());
                    showError("Failed to initialize map features.");
                }
            }
        });

        webView.getEngine().loadContent(htmlContent);

        // Initialize content pane
        VBox mainContentVBox = new VBox(10);
        mainContentVBox.setAlignment(Pos.TOP_CENTER);
        mainContentVBox.setPadding(new Insets(15));
        mainContentVBox.setStyle("-fx-background-color: white;");

        // Initial content - instructions
        Label instructions = new Label("Select a location on the map to view details");
        instructions.setFont(Font.font(16));
        mainContentVBox.getChildren().add(instructions);

        // Location title
        locationTitle = new Label();
        locationTitle.setFont(Font.font(20));
        locationTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        locationTitle.setAlignment(Pos.CENTER);
        locationTitle.setTextAlignment(TextAlignment.CENTER);
        locationTitle.setMaxWidth(Double.MAX_VALUE);
        locationTitle.setVisible(false);

        // Button container
        buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(10, 0, 15, 0));

        // Create back button
        backButton = createStyledButton("Back");
        backButton.setStyle("-fx-background-color: #e74c3c;"); // Red color for back button
        backButton.setOnMouseEntered(e -> backButton.setStyle("-fx-background-color: #c0392b;"));
        backButton.setOnMouseExited(e -> backButton.setStyle("-fx-background-color: #e74c3c;"));
        backButton.setVisible(false);

        // Create the three buttons using the styled method
        Button viewMediaButton = createStyledButton("View Media");
        Button takeQuizButton = createStyledButton("Take Quiz");
        Button viewInfoButton = createStyledButton("View Info");

        // Add buttons to the container
        buttonContainer.getChildren().addAll(backButton, viewMediaButton, takeQuizButton, viewInfoButton);
        buttonContainer.setVisible(false);

        // Location description
        locationDescription = new Label();
        locationDescription.setFont(Font.font(14));
        locationDescription.setStyle("-fx-text-fill: #555;");
        locationDescription.setWrapText(true);
        locationDescription.setLineSpacing(5);

        ScrollPane descriptionScroll = new ScrollPane(locationDescription);
        descriptionScroll.setFitToWidth(true);
        descriptionScroll.setPrefHeight(200);
        descriptionScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        // Media view setup - now using WebView for embedded videos
        mediaContainer = new VBox(10);
        mediaContainer.setAlignment(Pos.CENTER);
        mediaContainer.setVisible(false);
        mediaContainer.setPadding(new Insets(10));

        // Style the media container
        mediaContainer.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dddddd; -fx-border-radius: 5; -fx-padding: 10;");

        // Set preferred size for the container
        mediaContainer.setPrefWidth(600);
        mediaContainer.setPrefHeight(400);

        // Loading indicator for media
        mediaLoadingIndicator = new ProgressIndicator();
        mediaLoadingIndicator.setVisible(false);

        infoContainer = new VBox(10, descriptionScroll);
        infoContainer.setVisible(false);

        // Quiz section setup
        setupQuizSection();
        quizBox.setVisible(false);

        // Add all main components to mainContentVBox
        mainContentVBox.getChildren().addAll(locationTitle, buttonContainer, mediaLoadingIndicator, mediaContainer, infoContainer, quizBox);

        contentPane = new StackPane(mainContentVBox);
        contentPane.setStyle("-fx-background-color: white;");

        // Button actions
        backButton.setOnAction(e -> {
            // Reset to initial state
            locationTitle.setVisible(false);
            buttonContainer.setVisible(false);
            hideAllContentExcept(null);

            // Clear selection
            currentLocationId = null;
            currentTitle = null;
            currentDescription = null;

            // Hide back button
            backButton.setVisible(false);

            // Show instructions
            instructions.setVisible(true);
        });

        viewMediaButton.setOnAction(e -> {
            hideAllContentExcept(mediaContainer);
            showLocationMedia(currentLocationId);
        });

        takeQuizButton.setOnAction(e -> {
            hideAllContentExcept(quizBox);
            startQuizForLocation(currentLocationId, currentTitle);
        });

        viewInfoButton.setOnAction(e -> {
            hideAllContentExcept(infoContainer);
            openInfoWebsite(currentLocationId);
            locationDescription.setText(locationDescriptions.getOrDefault(currentLocationId, "No detailed description available."));
        });

        // In the setupMainContent method, adjust the split pane divider position
        splitPane.setDividerPositions(0.6); // Gives more space to the content side

        // Update the description scroll pane size
        descriptionScroll.setPrefHeight(150); // Reduced to make more room for media

        // Add components to split pane
        splitPane.getItems().addAll(webView, contentPane);
        root.setCenter(splitPane);
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; "
                + "-fx-padding: 8 15; -fx-background-radius: 5; -fx-cursor: hand;");

        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; "
                + "-fx-font-size: 14px; -fx-padding: 8 15; -fx-background-radius: 5; -fx-cursor: hand;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; "
                + "-fx-font-size: 14px; -fx-padding: 8 15; -fx-background-radius: 5; -fx-cursor: hand;"));

        return button;
    }

    private String extractVideoId(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        // Handle YouTube URLs
        if (url.contains("youtube.com") || url.contains("youtu.be")) {
            // Handle youtu.be short URLs
            if (url.contains("youtu.be/")) {
                String[] parts = url.split("youtu.be/");
                if (parts.length > 1) {
                    return parts[1].split("[?&#]")[0];
                }
            }

            // Handle standard YouTube URLs with v= parameter
            if (url.contains("v=")) {
                String[] parts = url.split("v=");
                if (parts.length > 1) {
                    return parts[1].split("[?&#]")[0];
                }
            }

            // Handle YouTube share URLs
            if (url.contains("youtube.com/watch")) {
                String[] parts = url.split("/watch\\?");
                if (parts.length > 1) {
                    String[] params = parts[1].split("&");
                    for (String param : params) {
                        if (param.startsWith("v=")) {
                            return param.substring(2).split("[&#]")[0];
                        }
                    }
                }
            }

            // Handle YouTube shorts URLs
            if (url.contains("youtube.com/shorts/")) {
                String[] parts = url.split("shorts/");
                if (parts.length > 1) {
                    return parts[1].split("[?&#]")[0];
                }
            }
        }

        return null;
    }
    private void showLocationMedia(String locationId) {
        mediaContainer.getChildren().clear();

        if (!locationMediaUrls.containsKey(locationId)) {
            showError("No media available for this location");
            return;
        }

        String url = locationMediaUrls.get(locationId);

        // Handle TikTok URLs differently (can't be embedded)
        if (url.contains("tiktok.com")) {
            hostServices.showDocument(url);
            return;
        }

        String videoId = extractVideoId(url);
        if (videoId == null) {
            showError("Could not parse video URL. Opening in browser instead...");
            hostServices.showDocument(url);
            return;
        }

        // Show loading indicator
        mediaLoadingIndicator.setVisible(true);
        mediaContainer.getChildren().add(mediaLoadingIndicator);

        WebView videoView = new WebView();

        // Make video responsive to container size
        videoView.setMinSize(300, 200);
        videoView.setPrefSize(560, 315);
        videoView.setMaxSize(800, 450);

        // Bind the WebView size to the container size with some padding
        videoView.prefWidthProperty().bind(mediaContainer.widthProperty().subtract(20));
        videoView.prefHeightProperty().bind(mediaContainer.heightProperty().subtract(20));

        videoView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                mediaLoadingIndicator.setVisible(false);
            } else if (newState == Worker.State.FAILED) {
                mediaLoadingIndicator.setVisible(false);
                showError("Failed to load embedded media. Opening in browser...");
                hostServices.showDocument(url);
            }
        });

        String embedHTML = String.format("""
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body {
                    margin: 0;
                    padding: 0;
                    overflow: hidden;
                }
                iframe {
                    width: 100%%;
                    height: 100%%;
                    border: none;
                }
            </style>
        </head>
        <body>
            <iframe src="https://www.youtube.com/embed/%s?autoplay=1" 
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
                    allowfullscreen></iframe>
        </body>
        </html>
        """, videoId);

        videoView.getEngine().loadContent(embedHTML);
        mediaContainer.getChildren().add(videoView);
        mediaContainer.setVisible(true);
    }

    private void hideAllContentExcept(Region visibleContent) {
        mediaContainer.setVisible(false);
        infoContainer.setVisible(false);
        quizBox.setVisible(false);
        mediaLoadingIndicator.setVisible(false);

        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        // Clear any existing media WebViews
        mediaContainer.getChildren().removeIf(node -> node instanceof WebView);

        if (visibleContent != null) {
            visibleContent.setVisible(true);
        }

        // Ensure the basic UI elements remain visible unless we're showing specific content
        if (visibleContent != mediaContainer && visibleContent != infoContainer && visibleContent != quizBox) {
            locationTitle.setVisible(true);
            buttonContainer.setVisible(true);
        }
    }

    // Java-JavaScript bridge class
    public class JavaConnector {
        public void showLocation(String locationId, String title, String description) {
            Platform.runLater(() -> {
                currentLocationId = locationId;
                currentTitle = title;
                currentDescription = description;
                locationTitle.setText(title);

                // Reset all quiz-related states
                resetQuizUI();

                // Make sure the basic UI elements are visible
                locationTitle.setVisible(true);
                buttonContainer.setVisible(true);

                // Show back button
                backButton.setVisible(true);

                // Hide instructions
                instructions.setVisible(false);

                // Hide all content containers except the basic info
                hideAllContentExcept(null);

                // Clear any previous description
                locationDescription.setText("");

                // Reset the media container
                mediaContainer.getChildren().clear();
                Label mediaPlaceholder = new Label("Click 'View Media' to see location video");
                mediaPlaceholder.setStyle("-fx-text-fill: #888; -fx-font-style: italic;");
                mediaContainer.getChildren().add(mediaPlaceholder);
            });
        }

        public void log(String message) {
            System.out.println("JS Log: " + message);
        }
    }

    private void resetQuizUI() {
        currentQuestionIndex = 0;
        score = 0;
        currentQuizQuestions = null;

        // Reset all quiz UI elements
        if (quizBox != null) {
            RadioButton[] options = (RadioButton[]) quizLabel.getUserData();
            for (RadioButton option : options) {
                option.setSelected(false);
                option.setDisable(false);
                option.setVisible(true);
                option.setStyle("-fx-font-size: 13px; -fx-wrap-text: true;");
            }

            quizLabel.setText("");
            resultLabel.setText("");
            scoreLabel.setText("");

            if (submitAnswerButton != null) {
                submitAnswerButton.setVisible(true);
            }
            if (nextQuestionButton != null) {
                nextQuestionButton.setVisible(false);
            }

            // Remove restart button if exists
            if (quizBox.getChildren().size() > 9) {
                quizBox.getChildren().remove(9);
            }
        }
    }

    private void openInfoWebsite(String locationId) {
        if (locationId != null && locationInfoUrls.containsKey(locationId)) {
            try {
                String url = locationInfoUrls.get(locationId);
                hostServices.showDocument(url);
            } catch (Exception e) {
                showError("Could not open information website for this location.");
                System.err.println("Error opening website: " + e.getMessage());
            }
        } else {
            showError("No information website available for this location.");
        }
    }

    private void setupQuizSection() {

        // Create quiz back button
        Button quizBackButton = createStyledButton("Back");
        quizBackButton.setStyle("-fx-background-color: #e74c3c;");
        quizBackButton.setOnMouseEntered(e -> quizBackButton.setStyle("-fx-background-color: #c0392b;"));
        quizBackButton.setOnMouseExited(e -> quizBackButton.setStyle("-fx-background-color: #e74c3c;"));
        quizBackButton.setOnAction(e -> {
            hideAllContentExcept(null);
            resetQuizUI();
        });

        quizLabel = new Label();
        quizLabel.setStyle("-fx-font-size: 14px; -fx-wrap-text: true;");
        quizLabel.setMaxWidth(Double.MAX_VALUE);

        RadioButton option1 = new RadioButton();
        RadioButton option2 = new RadioButton();
        RadioButton option3 = new RadioButton();
        option1.setStyle("-fx-font-size: 13px; -fx-wrap-text: true;");
        option2.setStyle("-fx-font-size: 13px; -fx-wrap-text: true;");
        option3.setStyle("-fx-font-size: 13px; -fx-wrap-text: true;");

        ToggleGroup group = new ToggleGroup();
        option1.setToggleGroup(group);
        option2.setToggleGroup(group);
        option3.setToggleGroup(group);

        submitAnswerButton = createStyledButton("Submit Answer");
        nextQuestionButton = createStyledButton("Next Question");
        nextQuestionButton.setVisible(false);

        resultLabel = new Label();
        resultLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        scoreLabel = new Label();
        scoreLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        submitAnswerButton.setOnAction(e -> checkAnswer(group, submitAnswerButton));
        nextQuestionButton.setOnAction(e -> showNextQuestion());

        quizBox = new VBox(8, quizLabel, option1, option2, option3,
                submitAnswerButton, resultLabel, scoreLabel, nextQuestionButton, quizBackButton);
        quizBox.setPadding(new Insets(15));
        quizBox.setStyle("-fx-background-color: #f8f8f8; -fx-border-radius: 10; -fx-border-color: #ddd;");
        quizLabel.setUserData(new RadioButton[]{option1, option2, option3});
    }

    private void checkAnswer(ToggleGroup group, Button submitAnswer) {
        RadioButton selected = (RadioButton) group.getSelectedToggle();
        if (selected != null) {
            RadioButton[] options = (RadioButton[]) quizLabel.getUserData();
            int selectedIndex = -1;
            if (selected == options[0]) {
                selectedIndex = 0;
            } else if (selected == options[1]) {
                selectedIndex = 1;
            } else if (selected == options[2]) {
                selectedIndex = 2;
            }

            QuizQuestion currentQuestion = currentQuizQuestions.get(currentQuestionIndex);
            if (selectedIndex == currentQuestion.correctAnswerIndex) {
                resultLabel.setText("Correct! ✔");
                resultLabel.setTextFill(Color.GREEN);
                score++;
            } else {
                resultLabel.setText("Incorrect ✖ - The correct answer is highlighted");
                resultLabel.setTextFill(Color.RED);
                // Highlight the correct answer
                options[currentQuestion.correctAnswerIndex].setStyle("-fx-font-size: 13px; -fx-wrap-text: true; -fx-text-fill: #2ecc71; -fx-font-weight: bold;");
            }

            // Update score display
            scoreLabel.setText("Score: " + score + "/" + (currentQuestionIndex + 1));
            scoreLabel.setTextFill(Color.DARKBLUE);

            // Disable all radio buttons after answer is submitted
            for (RadioButton option : options) {
                option.setDisable(true);
            }

            // Hide submit button and show next question button
            submitAnswer.setVisible(false);
            nextQuestionButton.setVisible(true);
        } else {
            resultLabel.setText("Please select an answer!");
            resultLabel.setTextFill(Color.ORANGE);
        }
    }

    private void showNextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < currentQuizQuestions.size()) {
            displayCurrentQuestion();
        } else {
            // Quiz completed
            quizLabel.setText("Quiz completed!\nYour final score: " + score + "/" + currentQuizQuestions.size());
            resultLabel.setText("");
            scoreLabel.setText("");

            // Hide options and buttons
            RadioButton[] options = (RadioButton[]) quizLabel.getUserData();
            for (RadioButton option : options) {
                option.setVisible(false);
            }

            nextQuestionButton.setVisible(false);
            submitAnswerButton.setVisible(false);

            // Show restart button
            restartQuizButton = createStyledButton("Restart Quiz");
            restartQuizButton.setOnAction(e -> startQuizForLocation(currentLocationId, currentTitle));

            // Remove previous restart button if exists
            if (quizBox.getChildren().size() > 10) { // Changed from 9 to 10
                quizBox.getChildren().remove(10);
            }
            quizBox.getChildren().add(restartQuizButton);
        }
    }

    private void displayCurrentQuestion() {
        QuizQuestion currentQuestion = currentQuizQuestions.get(currentQuestionIndex);

        // Reset UI for new question
        RadioButton[] options = (RadioButton[]) quizLabel.getUserData();
        for (RadioButton option : options) {
            option.setDisable(false);
            option.setSelected(false);
            option.setStyle("-fx-font-size: 13px; -fx-wrap-text: true;"); // Reset style
            option.setVisible(true);
        }

        quizLabel.setText(currentQuestion.question);
        options[0].setText(currentQuestion.options[0]);
        options[1].setText(currentQuestion.options[1]);
        options[2].setText(currentQuestion.options[2]);

        resultLabel.setText("");
        scoreLabel.setText("Score: " + score + "/" + currentQuestionIndex);

        // Show submit button and hide next button
        submitAnswerButton.setVisible(true);
        nextQuestionButton.setVisible(false);
    }

    private void startQuizForLocation(String locationId, String title) {
        currentQuestionIndex = 0;
        score = 0;
        currentQuizQuestions = getQuizQuestionsForLocation(locationId, title);

        // Reset UI elements
        RadioButton[] options = (RadioButton[]) quizLabel.getUserData();
        for (RadioButton option : options) {
            option.setVisible(true);
            option.setDisable(false);
            option.setSelected(false);
            option.setStyle("-fx-font-size: 13px; -fx-wrap-text: true;");
        }

        submitAnswerButton.setVisible(true);
        nextQuestionButton.setVisible(false);

        // Show back button
        backButton.setVisible(true);

        // Remove restart button if exists
        if (quizBox.getChildren().size() > 10) { // Changed from 9 to 10 because we added quizBackButton
            quizBox.getChildren().remove(10);
        }

        if (currentQuizQuestions != null && !currentQuizQuestions.isEmpty()) {
            displayCurrentQuestion();
        } else {
            quizLabel.setText("No quiz questions available for " + title);
            for (RadioButton option : options) {
                option.setVisible(false);
            }
            submitAnswerButton.setVisible(false);
        }
    }

    private List<QuizQuestion> getQuizQuestionsForLocation(String locationId, String title) {
        List<QuizQuestion> questions = new ArrayList<>();

        switch(locationId) {
            case "Maseru":
                questions.add(new QuizQuestion("What is Maseru's elevation above sea level?",
                        new String[]{"1,000 meters", "1,600 meters", "2,000 meters"}, 1));
                questions.add(new QuizQuestion("When was Maseru founded?",
                        new String[]{"1824", "1869", "1901"}, 1));
                questions.add(new QuizQuestion("What is Maseru's primary economic activity?",
                        new String[]{"Mining", "Government services", "Agriculture"}, 1));
                break;
            case "Teyateyaneng":
                questions.add(new QuizQuestion("What is Teyateyaneng famous for?",
                        new String[]{"Ski resorts", "Traditional crafts and Basotho hats", "High-tech industries"}, 1));
                questions.add(new QuizQuestion("What is the nickname of Teyateyaneng?",
                        new String[]{"TY", "The Craft City", "Hat Town"}, 0));
                questions.add(new QuizQuestion("Which industry is prominent in Teyateyaneng?",
                        new String[]{"Textile", "Mining", "Technology"}, 0));
                break;
            case "Butha-Buthe":
                questions.add(new QuizQuestion("What is Butha-Buthe known as?",
                        new String[]{"The industrial capital", "The gateway to Afriski", "The diamond mining town"}, 1));
                questions.add(new QuizQuestion("In which part of Lesotho is Butha-Buthe located?",
                        new String[]{"Northern", "Southern", "Eastern"}, 0));
                questions.add(new QuizQuestion("What is the main economic activity in Butha-Buthe?",
                        new String[]{"Agriculture", "Tourism", "Manufacturing"}, 1));
                break;
            case "Leribe":
                questions.add(new QuizQuestion("What is Leribe famous for?",
                        new String[]{"Its beaches", "Its agricultural production", "Its tech startups"}, 1));
                questions.add(new QuizQuestion("Which famous lodge is near Leribe?",
                        new String[]{"Maliba Lodge", "Afriski Lodge", "Katse Lodge"}, 0));
                questions.add(new QuizQuestion("What type of landscape surrounds Leribe?",
                        new String[]{"Desert", "Fertile farmlands", "Dense forest"}, 1));
                break;
            case "Pioneer-Mall":
                questions.add(new QuizQuestion("When was Pioneer Mall opened?",
                        new String[]{"2005", "2010", "2015"}, 1));
                questions.add(new QuizQuestion("How many stores does Pioneer Mall have?",
                        new String[]{"Over 50", "Over 70", "Over 100"}, 1));
                questions.add(new QuizQuestion("What is Pioneer Mall's claim to fame?",
                        new String[]{"First mall in Lesotho", "Largest mall in Lesotho", "Most luxurious mall"}, 1));
                break;
            case "Mafeteng-Mall":
                questions.add(new QuizQuestion("What is special about Mafeteng Mall?",
                        new String[]{"It's the smallest mall", "It has a popular food court", "It's underground"}, 1));
                questions.add(new QuizQuestion("What type of brands can be found in Mafeteng Mall?",
                        new String[]{"Local only", "Local and international", "International only"}, 1));
                questions.add(new QuizQuestion("What makes Mafeteng Mall a community space?",
                        new String[]{"Its size", "Its variety of stores", "Its gathering spaces"}, 2));
                break;
            case "Maqalika-Dam-and-Lake":
                questions.add(new QuizQuestion("What recreational activities are available at Maqalika Dam?",
                        new String[]{"Fishing and boating", "Skiing and snowboarding", "Rock climbing"}, 0));
                questions.add(new QuizQuestion("What is Maqalika Dam primarily used for?",
                        new String[]{"Water supply", "Electricity generation", "Tourism"}, 0));
                questions.add(new QuizQuestion("What wildlife activity is popular at Maqalika?",
                        new String[]{"Bird watching", "Lion spotting", "Whale watching"}, 0));
                break;
            case "Maletsunyane":
                questions.add(new QuizQuestion("What makes Maletsunyane Falls famous?",
                        new String[]{"It is the tallest waterfall in southern Africa", "It is a volcano", "It is Lesotho's largest lake"}, 0));
                questions.add(new QuizQuestion("How high is Maletsunyane Falls?",
                        new String[]{"100m", "192m", "250m"}, 1));
                questions.add(new QuizQuestion("What adventure activity is popular here?",
                        new String[]{"Abseiling", "Paragliding", "White-water rafting"}, 0));
                break;
            case "Lion-Rock":
                questions.add(new QuizQuestion("What is the best time to visit Lion Rock Mountain?",
                        new String[]{"Early morning for sunrise views", "Midday for warmest temperatures", "Late night for stargazing"}, 0));
                questions.add(new QuizQuestion("Why is it called Lion Rock?",
                        new String[]{"Lions live there", "It resembles a lion's head", "A lion was found there"}, 1));
                questions.add(new QuizQuestion("What makes Lion Rock a geological wonder?",
                        new String[]{"Its unique shape", "Its rare minerals", "Its volcanic origin"}, 0));
                break;
            case "Lancers-Gap":
                questions.add(new QuizQuestion("What is Lancer's Gap known for?",
                        new String[]{"Its shopping centers", "Its scenic mountain views", "Its gold mines"}, 1));
                questions.add(new QuizQuestion("What historical significance does Lancer's Gap have?",
                        new String[]{"Military history", "Trade route", "Religious site"}, 0));
                questions.add(new QuizQuestion("What can you see from Lancer's Gap?",
                        new String[]{"Maseru", "Johannesburg", "Durban"}, 0));
                break;
            case "Afriski":
                questions.add(new QuizQuestion("What is Afriski known for?",
                        new String[]{"Being Lesotho's only ski resort", "Having the highest casino in Africa", "Being a desert oasis"}, 0));
                questions.add(new QuizQuestion("What unique feature does Afriski have?",
                        new String[]{"Highest pub in Africa", "Longest ski slope", "Coldest temperatures"}, 0));
                questions.add(new QuizQuestion("What activities are available year-round?",
                        new String[]{"Hiking and mountain biking", "Skiing and snowboarding", "Swimming and sunbathing"}, 0));
                break;
            case "Thaba-Bosiu":
                questions.add(new QuizQuestion("What is the significance of Thaba Bosiu?",
                        new String[]{"It is a shopping center", "It is the birthplace of Moshoeshoe I", "It is the historical mountain fortress of the Basotho"}, 2));
                questions.add(new QuizQuestion("When was Thaba Bosiu founded?",
                        new String[]{"1800", "1824", "1850"}, 1));
                questions.add(new QuizQuestion("What UNESCO designation does Thaba Bosiu have?",
                        new String[]{"World Heritage Site", "Biosphere Reserve", "Global Geopark"}, 0));
                break;
            case "Morija-Museum":
                questions.add(new QuizQuestion("What annual event happens in Morija?",
                        new String[]{"Morija Arts & Cultural Festival", "Lesotho Music Awards", "Basotho Fashion Week"}, 0));
                questions.add(new QuizQuestion("What is special about Morija Museum?",
                        new String[]{"Oldest in Lesotho", "Largest in Africa", "Focuses only on dinosaurs"}, 0));
                questions.add(new QuizQuestion("What can you find in Morija Museum?",
                        new String[]{"Historical artifacts", "Modern art", "Science exhibits"}, 0));
                break;
            case "Ha-Kome":
                questions.add(new QuizQuestion("What are the Ha Kome Cave Houses?",
                        new String[]{"Modern luxury homes", "Ancient cave dwellings", "Underground shopping center"}, 1));
                questions.add(new QuizQuestion("What UNESCO designation do they have?",
                        new String[]{"World Heritage Site", "Tentative List", "None"}, 1));
                questions.add(new QuizQuestion("What makes them unique?",
                        new String[]{"Traditional architecture", "Modern design", "Underground location"}, 0));
                break;
            case "Katse-Dam":
                questions.add(new QuizQuestion("What is the primary purpose of Katse Dam?",
                        new String[]{"Hydroelectric power", "Water supply to South Africa", "Tourist attraction"}, 1));
                questions.add(new QuizQuestion("How high is Katse Dam?",
                        new String[]{"150m", "185m", "210m"}, 1));
                questions.add(new QuizQuestion("What project is Katse Dam part of?",
                        new String[]{"Lesotho Highlands Water Project", "Lesotho Energy Project", "Lesotho Tourism Initiative"}, 0));
                break;
            case "Mohale-Dam":
                questions.add(new QuizQuestion("What project is Mohale Dam part of?",
                        new String[]{"Lesotho Highlands Water Project", "Lesotho Tourism Development", "Lesotho Energy Initiative"}, 0));
                questions.add(new QuizQuestion("How does Mohale Dam rank in size in Lesotho?",
                        new String[]{"Largest", "Second largest", "Third largest"}, 1));
                questions.add(new QuizQuestion("What is the setting of Mohale Dam?",
                        new String[]{"Mountainous", "Desert", "Forest"}, 0));
                break;
            default:
                questions.add(new QuizQuestion("What do you find interesting about " + title + "?",
                        new String[]{"Its history", "Its natural beauty", "Its cultural significance"}, 0));
                questions.add(new QuizQuestion("What would you like to know more about " + title + "?",
                        new String[]{"Historical facts", "Tourist activities", "Local culture"}, 0));
        }

        return questions;
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        });
    }

    @Override
    public void stop() {
        // Clean up media resources
        mediaContainer.getChildren().forEach(node -> {
            if (node instanceof WebView) {
                ((WebView)node).getEngine().load(null);
            }
        });

        // Clean up main WebView
        if (webView != null) {
            webView.getEngine().load(null);
        }

        // Clean up media player
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
    }

    // Inner class to represent quiz questions
    private static class QuizQuestion {
        String question;
        String[] options;
        int correctAnswerIndex;

        QuizQuestion(String question, String[] options, int correctAnswerIndex) {
            this.question = question;
            this.options = options;
            this.correctAnswerIndex = correctAnswerIndex;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
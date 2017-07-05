package guiPack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class GuiMain extends Application{
	
	Stage promptStage = new Stage();//used for gen text prompt
	Stage calendarStage = new Stage();
	
	DatePicker datePicker1 = new DatePicker();
	
	private BorderPane border = new BorderPane();
	
	private HBox bottomBar = new HBox(); //the bar that is ultimately put on the bottom, contains both HBoxes
	private HBox infoBar = new HBox(); //stores most values
	private HBox spellCheckBar = new HBox(); //stores only last word spelling label
	private ProgressBar progressBar = new ProgressBar();
	
	private Label fleschLabel = new Label("Flesch Score: ");
	private Label numWordsLabel = new Label("Words: ");
	private Label numSyllablesLabel = new Label("Syllables: ");
	private Label numSentencesLabel = new Label("Sentences: ");
	
	private Label stockLabel = new Label();
	
	private Label spellCheckLabel = new Label();
	
	private MenuBar menuBar = new MenuBar();
	//File Menu Items
	private Menu fileMenu = new Menu("File");
	private MenuItem importFileMenuItem =  new MenuItem("Open");
	private MenuItem saveCountsMenuItem =  new MenuItem("Save Counts");
	private MenuItem newMenuItem = new MenuItem("New");
	private MenuItem saveMenuItem = new MenuItem("Save Text");
	private MenuItem exitMenuItem = new MenuItem("Exit");
	//Generate Menu Items
	private Menu generateMenu = new Menu("Generate");
	private MenuItem generateTextItem = new MenuItem("Generate Text");
	private MenuItem retrainMenuItem = new MenuItem("Re-Train");
	//Stock Menu Items
	private Menu stockMenu = new Menu("Stock");
	private MenuItem displayStockItem = new MenuItem("Display Stock");
	//Edit Menu Items
	private Menu editMenu = new Menu("Edit");
	private MenuItem analyseTextItem = new MenuItem("Analyze Text Area");
	private CheckMenuItem wordCountToggle = new CheckMenuItem("Word Count");
	private CheckMenuItem sentenceCountToggle = new CheckMenuItem("Sentence Count");
	private CheckMenuItem syllableCountToggle = new CheckMenuItem("Syllable Count");
	private CheckMenuItem fleschScoreToggle = new CheckMenuItem("Flesch Score");
	//Sub-Menu Items
	private Menu truncateMenu = new Menu("Truncate");
	private MenuItem truncateMenuItem = new MenuItem("Truncate Text Area");
	private MenuItem displayImportData = new MenuItem("Show Import Data");
	private MenuItem analyseAllMenuItem = new MenuItem("Analyze all partial files");
	private MenuItem[] tfMenuItemArray = new MenuItem[10]; //an array of menu items for each of the parsed files, under truncate menu
	
	
	private TextArea textArea = new TextArea();
	
	private FileChooser fileChooser = new FileChooser();
	
	double[] times1 = new double[10]; //stores times to complete readability 1 for use in the graph
	double[] times2 = new double[10]; //stores times for r2
	
	//stores readability objects for each of the parsed files, used when importing counts of already analysed parsed files
	Readability1[] r1Array = new Readability1[10];
	Readability2[] r2Array = new Readability2[10];
	
	private File curFile;//stores the current file, used to check if the file is a parsed file
	
	private double fleschScore; //stores current scores to be displayed
	private double numWords; 
	private double numSyllables;
	private double numSentences;
	
	boolean parsedFilesExists = false; //for checking if file has been parsed yet
	boolean[] parsedFilesAnalysed = new boolean[10]; //an array that stores booleans to see if the parsed files have been analysed
	boolean fileAnalysed = false; //boolean to see if the file currently loaded into the text area has been analysed
	
	//different warnings for some tasks
	private Alert fileNotAnalysedAlert = new Alert(AlertType.WARNING);
	private Alert textAreaBlankAlert = new Alert(AlertType.WARNING);
	private Alert parseFileAlert = new Alert(AlertType.WARNING);
	private Alert noFilesAnalysedAlert = new Alert(AlertType.WARNING);
	private Alert dateNotFoundAlert = new Alert(AlertType.WARNING);
	private Alert wordNotInDatabaseAlert = new Alert(AlertType.WARNING);
	private Alert nonNumericalAlert = new Alert(AlertType.WARNING);
	private Alert lessThan0Alert = new Alert(AlertType.WARNING);
	//private Alert 
	private Alert fileSavedAlert = new Alert(AlertType.INFORMATION);
	
	//Data Bases Uses For Actions
	private StockDataBase stockDB;
	private Dictionary dictionary;
	private ParagraphGenerator textGen;
	
	@Override
	public void start(Stage primaryStage) {
		
		setAlertText(); //sets alert messages
		
		buildDataObjects(); //builds stockDB, dictionary, 
		
		//initializes the names for each of the parsed file load buttons
		for(int i = 0; i < 10; i++){
			tfMenuItemArray[i] = new MenuItem("Import %" + ((i+1)*10) + " File"); //names menu item
			tfMenuItemArray[i].setGraphic(new Circle(5, Color.RED)); //gives menu item red items
		}
		
		try {
			
			//disables generate menu until data structure is built, can take a few seconds
			generateTextItem.setDisable(true);
			retrainMenuItem.setDisable(true);
			
			//changes toggles to false
			wordCountToggle.setSelected(false);
			sentenceCountToggle.setSelected(false);
			syllableCountToggle.setSelected(false);
			fleschScoreToggle.setSelected(false);
			
			//sets spacing for bottom hbox that holds counts/loading bar
			infoBar.setMinSize(20, 25);
			infoBar.setSpacing(20);
			infoBar.setPadding(new Insets(1,1,1,3));
			
			//text area formating
			textArea.setWrapText(true);
			textArea.setFont(new Font("Serif", 20));
			
			//gives menu items there corresponding colored circle
			truncateMenuItem.setGraphic(new Circle(5, Color.web("#ffd84f")));
			analyseTextItem.setGraphic(new Circle(5, Color.web("#ffd84f")));
			//displayImportData.setGraphic(new Circle(5, Color.RED));
			analyseAllMenuItem.setGraphic(new Circle(5, Color.RED));
			
			fleschScoreToggle.setGraphic(new Circle(5, Color.RED));
			wordCountToggle.setGraphic(new Circle(5, Color.RED));
			syllableCountToggle.setGraphic(new Circle(5, Color.RED));
			sentenceCountToggle.setGraphic(new Circle(5, Color.RED));
			
			//initializes menu item actions
			setFileItemsAction();
			setEditItemsAction();
			setGenerateItemsAction();
			setStockItemsAction();
			
			textArea.textProperty().addListener(new ChangeListener<String>() { //does things when text area is changed
			    @Override
			    public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
			    	
			    	runSpellCheck(newValue, (oldValue.length() > newValue.length())); //sends a boolean that is true if a delete action occurred
			    	textChangeReset(); //resets counts and parsed files on change in text area
			    }
			});
			
			
			spellCheckBar.getChildren().add(spellCheckLabel);
			bottomBar.getChildren().addAll(infoBar, spellCheckBar);
			
			//adds items to gui
			
			border.setTop(menuBar);
			border.setCenter(textArea);
			border.setBottom(bottomBar);
			
			//adds items to menus
			menuBar.getMenus().addAll(fileMenu, editMenu, generateMenu, stockMenu);
			fileMenu.getItems().addAll(importFileMenuItem, newMenuItem, saveMenuItem, saveCountsMenuItem, exitMenuItem);
			generateMenu.getItems().addAll(generateTextItem, retrainMenuItem);
			stockMenu.getItems().addAll(displayStockItem);
			truncateMenu.getItems().add(truncateMenuItem);
			truncateMenu.getItems().add(displayImportData);
			truncateMenu.getItems().add(analyseAllMenuItem);
			
			for(int i = 0; i < 10; i++){ //adds all the parsed file buttons in a loop
				truncateMenu.getItems().add(tfMenuItemArray[i]);
			}
			
			editMenu.getItems().addAll(analyseTextItem, truncateMenu, wordCountToggle, sentenceCountToggle, syllableCountToggle, fleschScoreToggle);
			
			
			//BorderPane root = new BorderPane();
			Scene scene = new Scene(border,800,800);
			
			infoBar.setPrefWidth(scene.getWidth() * .75); //sets width of info bar to 3/4 of total bottom area
			scene.widthProperty().addListener(new ChangeListener<Number>(){ //resizes bottom bar on window resize to 3/4 of the window
				 @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
					 infoBar.setPrefWidth(scene.getWidth() * .75);
				    }
			});
			
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Text Analyzer");
			primaryStage.show();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void runSpellCheck(String text, boolean deleteAction){
		
		//long before = System.currentTimeMillis(); //used for testing lookup time
		
		int caretPos = textArea.getCaretPosition()-1;
		
		if(deleteAction){ //decrements on delete, otherwise position would be 1 too large
			caretPos--;
		}
		if(caretPos == -1 || text.length() == 0){ //returns if text area is blank
			return;
		}
		
		//starts counts on caret position
		int wordStart = caretPos;
		int wordEnd = caretPos;
		
		try{
			while(wordStart > 0 && (text.charAt(wordStart) != ' ' 
									&& text.charAt(wordStart) != '\n' 
									&& text.charAt(wordEnd) != '\t')){//ends on a space, new line, or tab
				wordStart--;//decrements wordStart
			}
			while(wordEnd < text.length() && (text.charAt(wordEnd) != ' ' 
												&& text.charAt(wordEnd) != '\n' 
												&& text.charAt(wordEnd) != '\t')){//ends on a space, new line, or tab
				wordEnd++;//increments word end
			}
		}catch(Exception e){
			//System.out.println("Large delete!");
			return; //a large import/delete event occurred causing the array to go out of bounds, just returning to ignore this event
		}
		String curWord = text.substring(wordStart, wordEnd); //gets word from text area
		
		if(curWord.equals("")){//if after cropping the curword is blank, return
			return;
		}
		if(curWord.charAt(0) == '\n' || curWord.charAt(0) == '\t'){ //crops out new line char if there is any
			curWord = curWord.substring(1, curWord.length());
		}
		
		//System.out.println(curWord);
		String cWord = curWord.toLowerCase().replaceAll("[^\\w']|\\d", "");//replaces all non alpa. chars with blank char
		
		if(dictionary.checkWord(cWord)){ //if word is found
			spellCheckLabel.setText("Correct: " + curWord);
		}else{
			spellCheckLabel.setText("Incorrect: " + curWord);
		}
		//System.out.println("Exec Time: " + (System.currentTimeMillis() - before));
		
	}
	
	public void textChangeReset(){ //resets counts and other things upon a change in the text area
		boolean parsedFileLoaded = curFileIsParsedFile(); //checks if file is one of the parsed files
        if((fileAnalysed || parsedFilesExists) && !parsedFileLoaded){ //if new unique file
        	//resets everything
        	parsedFilesExists = false;
        	fileAnalysed = false;
        	setPartialFilesRed();
        	truncateMenuItem.setGraphic(new Circle(5, Color.web("#ffd84f")));
        	analyseTextItem.setGraphic(new Circle(5, Color.web("#ffd84f")));
        	parsedFilesAnalysed = new boolean[10]; //resets partial files to false
        	changeCountsRed();
        	//curFile = null;
        }else if((parsedFilesExists) && parsedFileLoaded){ //if new truncated file loaded
        	int numFile = -1; //set to -1 temporarily
        	//System.out.println("Parsed file exists - comparing: " + curFile.getName());
        	
			for(int i = 0; i < 10; i++){//this loop finds what file it is
				if(curFile != null && curFile.getName().equals("parsedFile" + i + ".txt")){
					numFile = i;
					break;
				}
			}
			if(!parsedFilesAnalysed[numFile]){//if parsed file that was found isn't analyzed
        	fileAnalysed = false;
        	analyseTextItem.setGraphic(new Circle(5, Color.web("#ffd84f")));
        	changeCountsRed();
			}else{//if parsed file is analyzed
				//loads saved counts for that file
				fleschScore = r2Array[numFile].getFleschScore();
				numWords = r2Array[numFile].getNumWords();
				numSyllables =  r2Array[numFile].getNumSyllables();
				numSentences = r2Array[numFile].getNumSentences();
				//updates gui to display correct colors
				updateDisplayData();
				changeCountsGreen();
				analyseTextItem.setGraphic(new Circle(5, Color.GREEN));
			}
        }
	}
	
	public void setAlertText(){
		//set values for alerts
				fileSavedAlert.setTitle("File");
				fileSavedAlert.setHeaderText("File Has been saved.");
				
				noFilesAnalysedAlert.setTitle("File");
				noFilesAnalysedAlert.setHeaderText("Files not analysed.");
				noFilesAnalysedAlert.setContentText("Analyze truncated files to save the counts.");
				
				parseFileAlert.setTitle("Truncate File");
				parseFileAlert.setHeaderText("Truncated Files not created yet.");
				parseFileAlert.setContentText("Truncate files before continuing.");
				
				textAreaBlankAlert.setTitle("Text");
				textAreaBlankAlert.setHeaderText("Text area is blank.");
				textAreaBlankAlert.setContentText("Try entering some text!");
				
				fileNotAnalysedAlert.setTitle("File");
				fileNotAnalysedAlert.setHeaderText("The text has not been analysed");
				fileNotAnalysedAlert.setContentText("Can't generate value until the text has been analysed.");
				
				dateNotFoundAlert.setTitle("Stocks");
				
				nonNumericalAlert.setTitle("Error");
				nonNumericalAlert.setHeaderText("Number entered is invalid");
				nonNumericalAlert.setContentText("Please make sure you have entered a positive whole number. Press ok to continue.");
				
				wordNotInDatabaseAlert.setTitle("Error");
				wordNotInDatabaseAlert.setHeaderText("Starting word not in database");
				wordNotInDatabaseAlert.setContentText("The word you have entered is not in the database. Try the pick for me button. Press ok to continue.");
				
				lessThan0Alert.setTitle("Error");
				lessThan0Alert.setHeaderText("The Number Is Less than 1");
				lessThan0Alert.setContentText("Please make sure you have entered a whole number greater than 0. Press ok to continue.");
				
	}
	
	public void setFileItemsAction(){
		newMenuItem.setOnAction(e->{//new, sets text area to blank along with the needed resets
			textArea.setText(""); //sets text area to be blank
			parsedFilesExists = false; //resets parsed file boolean
        	fileAnalysed = false; //makes sure file isn't read as analyzed
        	setPartialFilesRed(); //gives all parsed files red circle
        	truncateMenuItem.setGraphic(new Circle(5, Color.web("#ffd84f")));//gives yellow circle
        	analyseTextItem.setGraphic(new Circle(5, Color.web("#ffd84f")));//gives yellow circle
        	parsedFilesAnalysed = new boolean[10]; //resets partial files to false
        	changeCountsRed(); //gives count menu items red circles
        	curFile = null; //cur file is set to null
        	textArea.setEditable(true);
		});
		
		exitMenuItem.setOnAction(e->{
			System.exit(0); //exits
		});
		
		//initializes action for 'Open' menu item
		importFileMenuItem.setOnAction(e->{
			File file = fileChooser.showOpenDialog(new Stage()); //shows file chooser
			textArea.setEditable(true);//re-enables text area after parsing
            if (file != null) {
            	curFile = file; //sets cur file
                importFile(file);//imports text to text area
            }
		});
		
		saveMenuItem.setOnAction(e->{
			File file = fileChooser.showSaveDialog(new Stage()); //displays file chooser
			textArea.setEditable(true);//re-enables text area after parsing
            if (file != null) {
            	File f2 = new File(file.getPath()+".txt"); //adds .txt to end of file path
            	saveText(textArea.getText(), f2); //calls save method
            }
		});
		
		saveCountsMenuItem.setOnAction(e->{
			StringBuilder sb = new StringBuilder(); //string that will eventually be saved
			boolean foundCount = false; //used to check if there is anything to save
			for(int i = 0; i < 10; i++){// loops through each possible file
				if(parsedFilesAnalysed[i]){
					foundCount = true; //at least 1 file exists
					sb.append("File: " + ((i+1)*10) + "\n");
					sb.append("Words: " + r2Array[i].getNumWords() + "\n");
					sb.append("Sentences: " + r2Array[i].getNumSentences() + "\n");
					sb.append("Syllables: " + r2Array[i].getNumSyllables() + "\n");
					sb.append("Flesch Score: " + r2Array[i].getFleschScore() + "\n");
					sb.append("\n");
				}
			}
			if(foundCount){//at least 1 file was found
				try {
					BufferedWriter br = new BufferedWriter(new FileWriter(new File("outputData/results.txt"))); //buffered reader with path hard coded
					br.write(sb.toString()); //saves string
					br.close(); //closes
					fileSavedAlert.showAndWait(); // displays dialog saying its been saved
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}else{ //no files have been found that have been analyzed
				noFilesAnalysedAlert.showAndWait(); //shows error message
			}
		});
	}
	
	public void setEditItemsAction(){
		
		fleschScoreToggle.selectedProperty().addListener(new ChangeListener<Boolean>() {
	        public void changed(ObservableValue ov,
	                Boolean old_val, Boolean new_val) {
	        			if(!fileAnalysed && new_val){//if text area has NOT been analyzed and they are trying to check button
	        				fileNotAnalysedAlert.showAndWait(); //shows error message
	        				fleschScoreToggle.setSelected(false); // changes button to be false (off)
	        			}
	        			else if(new_val){ //if text area has been analyzed and they are trying to check button
	        				infoBar.getChildren().remove(stockLabel); //removes the stock label if there
	        				infoBar.getChildren().add(fleschLabel); //adds label
	        			}
	        			else{
	        				infoBar.getChildren().remove(fleschLabel); //removes label
	        			}
	                }
	            });
		
		wordCountToggle.selectedProperty().addListener(new ChangeListener<Boolean>() {
	        public void changed(ObservableValue ov,
	                Boolean old_val, Boolean new_val) {
	        			if(!fileAnalysed && new_val){
	        				fileNotAnalysedAlert.showAndWait();
	        				wordCountToggle.setSelected(false);
	        			}
	        			else if(new_val){
	        				infoBar.getChildren().remove(stockLabel);
	        				infoBar.getChildren().add(numWordsLabel);
	        			}
	        			else{
	        				infoBar.getChildren().remove(numWordsLabel);
	        			}
	                }
	            });
		
		syllableCountToggle.selectedProperty().addListener(new ChangeListener<Boolean>() {
	        public void changed(ObservableValue ov,
	                Boolean old_val, Boolean new_val) {
			        	if(!fileAnalysed && new_val){
			        		fileNotAnalysedAlert.showAndWait();
			        		syllableCountToggle.setSelected(false);
	        			}
			        	else if(new_val){
			        		infoBar.getChildren().remove(stockLabel);
	        				infoBar.getChildren().add(numSyllablesLabel);
	        			}
	        			else{
	        				infoBar.getChildren().remove(numSyllablesLabel);
	        			}
	                }
	            });
		
		sentenceCountToggle.selectedProperty().addListener(new ChangeListener<Boolean>() {
	        public void changed(ObservableValue ov,
	                Boolean old_val, Boolean new_val) {
		        	if(!fileAnalysed && new_val){
		        		fileNotAnalysedAlert.showAndWait();
		        		sentenceCountToggle.setSelected(false);
        			}
		        	else if(new_val){
		        		infoBar.getChildren().remove(stockLabel);
        				infoBar.getChildren().add(numSentencesLabel);
        			}
        			else{
        				infoBar.getChildren().remove(numSentencesLabel);
        			}
		                }
	            });
		
		analyseAllMenuItem.setOnAction(e->{ //button to analyze all files
			if(!parsedFilesExists){ //if file hasn't been parsed
				parseFileAlert.showAndWait(); //show error message
			}else{
				//for use after files have been analyzed, will store current state temporarily
				File tempFile = curFile;
				boolean fileAnalyzedTemp = fileAnalysed;
				double fleschScoreTemp = fleschScore; 
				double numWordsTemp = numWords; 
				double numSyllablesTemp = numSyllables;
				double numSentencesTemp = numSentences;
				
				
				for(int i = 0; i < 10; i++){ //loops through 10 times for each of the 10 parsed files
					if(!parsedFilesAnalysed[i]){ //checks to see if file has already been analysed, if it has, its skipped
						int j = i; //puts i value in j, to get around compiler complaining about it not being a static value
						
						Task<String> task = new Task<String>() { //defines a new thread
							@SuppressWarnings("finally")
							@Override public String call() {
					    		StringBuilder sb = new StringBuilder(); //string builder will hold the text file that is pulled from text file
					    		try{
					    			//new buffered reader for the parsed file
						    		BufferedReader br = new BufferedReader(new FileReader(new File("inputData/parsedFile" + j + ".txt"))); 
						    		String line; //stores current line
						    		while((line = br.readLine()) != null){//reads lines until it is null
						    			sb.append(line); // line is added to overall doc string
						    			sb.append("\n"); //adds a new line
						    		}
						    		br.close(); //closes buffered reader on completion
					    		}catch (IOException e) {
									e.printStackTrace();
								}finally{
									curFile = new File("inputData/parsedFile" + j + ".txt"); //sets current file to file that was just read
									
									Task<Readability1> task1 = new Task<Readability1>() { //initializes readability 1 calculation in new tread
								    	@Override public Readability1 call() {
								    		Readability1 r1 = new Readability1(sb.toString());
								    		r1.getFleschScore(); //flesch score call will calc all values and time
								    		return r1;//returns readability object when called by event handler
								    	}
									};
									
									
								
									task1.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, //tasks to be done after readability 1 is completed
									        new EventHandler<WorkerStateEvent>() {
									    @Override
									    public void handle(WorkerStateEvent t) {
									    	Readability1 r1 = task1.getValue(); //gets readability object from thread
									    	times1[j] = r1.getTimeToComplete();//stores time to complete in array
											r1Array[j] = r1;//stores r1 object to be called upon if file is reloaded by user
									    }
									});
									
									Task<Readability2> task2 = new Task<Readability2>() {//initializes readability 2 calculation in new tread
									    @Override public Readability2 call() {
									    	Readability2 r2 = new Readability2(sb.toString());//r2 object has all counts calculated upon object creation
									        return r2;
									    }
									};
									task2.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, //tasks to be done after readability 2 is completed
									        new EventHandler<WorkerStateEvent>() {
									    @Override
									    public void handle(WorkerStateEvent t) {
									    	
									    	Readability2 r2 = task2.getValue(); //gets r2 value from thread
									    	
											times2[j] = r2.getTimeToComplete();//time to complete is saved
											
											parsedFilesAnalysed[j] = true;
											
											tfMenuItemArray[j].setGraphic(new Circle(5, Color.GREEN));
											r2Array[j] = r2;
											
											/*
											 * just added
											 */

									    	if(allFilesAnalysed()){//checks if the file is the last to be analyzed
										    	
										    	int numFile = -1;
										    	for(int i = 0; i < 10; i++){//this loop finds what file it is
													if(tempFile != null && tempFile.getName().equals("parsedFile" + i + ".txt")){
														numFile = i;
														break;
													}
												}
										    	if(numFile != -1){ //checks to see if its a parsed file
										    		//sets gui
										    		curFile = tempFile;
										    		analyseTextItem.setGraphic(new Circle(5, Color.GREEN));
													changeCountsGreen();
													
													//sets counts to file counts
													fleschScore = r2Array[numFile].getFleschScore(); 
													numWords = r2Array[numFile].getNumWords(); 
													numSyllables = r2Array[numFile].getNumSyllables();
													numSentences = r2Array[numFile].getNumSentences();
													
													updateDisplayData();
													fileAnalysed = true;
										    		
										    	}else{
										    		//returns states to what they were before the files were analyzed
											    	curFile = tempFile;
											    	fileAnalysed = fileAnalyzedTemp;
													fleschScore = fleschScoreTemp; 
													numWords = numWordsTemp; 
													numSyllables = numSyllablesTemp;
													numSentences = numSentencesTemp;
													updateDisplayData();
													
													if(!fileAnalysed){
														changeCountsRed();
													}
										    	}
										    }
											
									    }
									});
									
									//starts threads that calculate readability
									new Thread(task2).start();
									new Thread(task1).start();
									
									return sb.toString(); //returns the file from the outer thread, not used currently, but left in in case 
														//I wanted to load the text into something else in future
								}
					    	}
						};
						
						task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, //tasks to complete upon completion of outer thread
						        new EventHandler<WorkerStateEvent>() {
						    @Override
						    public void handle(WorkerStateEvent t) {
						    	analyseAllMenuItem.setGraphic(new Circle(5, Color.GREEN)); //sets circle to green
						    }
						});
						
						new Thread(task).start();//starts the outer thread
					}
				}
				
			}
			
		});
		
		for(int i = 0; i < 10; i++){ //initializes the action of the parsed file menu items
			int j = i; //makes compiler happy
				tfMenuItemArray[i].setOnAction(e->{
					if(parsedFilesExists){ //if file has been parsed
						curFile = new File("inputData/parsedFile"+j+".txt"); //sets file associated with button
						if(parsedFilesAnalysed[j]){
							fileAnalysed = true;
						}
						importFile(curFile); //imports the file
						textArea.setEditable(false);//need to re-enable after opening or new file
					}else{
						parseFileAlert.showAndWait(); //shows error
					}
				});
		}
		
		//initializes some menu items
		displayImportData.setOnAction(e->{
			displayChart(new Stage());
		});
		
		truncateMenuItem.setOnAction(e->{
			parseText(textArea.getText());
		});
		
		analyseTextItem.setOnAction(e->{
			analyzeText(textArea.getText());
		});
	}
	
	public void setGenerateItemsAction(){
		
		genPromptStage(); //builds stage that prompts the user when they hit the gen button
		
		generateTextItem.setOnAction(e->{
			
			promptStage.show(); //shows stage of prompt
			
		});
		
		retrainMenuItem.setOnAction(e->{
			File genFile = fileChooser.showOpenDialog(new Stage()); //shows file chooser
			textArea.setEditable(true);//re-enables text area after parsing
            if (genFile == null) {
            	return;//if file doesn't exist
            	//maybe add warning msg
            }
			retrainMenuItem.setDisable(true);//stops user from selecting gen menu while in use
			generateTextItem.setDisable(true);
			Task<ParagraphGenerator> rTask = new Task<ParagraphGenerator>(){
				@Override
				protected ParagraphGenerator call() throws Exception {
					ParagraphGenerator textGenNew = new ParagraphGenerator(genFile); //builds text gen
					//textGenNew.displayDataStructure();
					return textGenNew; //returns it to main
				}
			};
			
			rTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, //done after textGen is built
			        new EventHandler<WorkerStateEvent>() {
			    @Override
			    public void handle(WorkerStateEvent t) {
			    	textGen = rTask.getValue(); //gets new initialized text editor
			    	retrainMenuItem.setDisable(false);//re-enables buttons
					generateTextItem.setDisable(false);
			    }
			});
			
			new Thread(rTask).start();
		});
		
	}
	
	public void setStockItemsAction(){
		
		genCalendarStage();
		
		displayStockItem.setOnAction(e->{
			calendarStage.show();
		});
	}
	
	public void buildDataObjects(){
		
		Task<ParagraphGenerator> makeGen = new Task<ParagraphGenerator>(){
			@Override
			protected ParagraphGenerator call() throws Exception {
				return new ParagraphGenerator(new File("generateTextFolder/warAndPeace100k.txt"));
			}
		};
		Task<Dictionary> makeDict = new Task<Dictionary>(){
			@Override
			protected Dictionary call() throws Exception {
				return new Dictionary();
			}
		};
		Task<StockDataBase> makeStock = new Task<StockDataBase>(){
			@Override
			protected StockDataBase call() throws Exception {
				return new StockDataBase();
			}
		};
		
		makeGen.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
		        new EventHandler<WorkerStateEvent>() {
		    @Override
		    public void handle(WorkerStateEvent t) {
		    	textGen = makeGen.getValue(); //gets new initialized text editor
		    	//textGen.displayDataStructure();
		 
		    	//re-enables menu items when they have a db to work with
		    	retrainMenuItem.setDisable(false);
		    	generateTextItem.setDisable(false);
		    }
		});
		makeDict.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, 
		        new EventHandler<WorkerStateEvent>() {
		    @Override
		    public void handle(WorkerStateEvent t) {
		    	dictionary = makeDict.getValue(); //gets new initialized dictionary
		    }
		});
		makeStock.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
		        new EventHandler<WorkerStateEvent>() {
		    @Override
		    public void handle(WorkerStateEvent t) {
		    	stockDB = makeStock.getValue(); //gets new initialized stockDB
		    }
		});
		
		new Thread(makeGen).start();
		new Thread(makeDict).start();
		new Thread(makeStock).start();
		
	}
	
	public void genPromptStage(){ //this builds the word generator prompt
		
		Label wordAreaLabel = new Label("Number of words to generate:");
		Label startingWordLabel = new Label("Pick a word to start generating from:");
		
		Button genButton = new Button("Generate Text");
		Button cancelButton = new Button("Cancel");
		Button pickForMeButton =  new Button("Pick for me");
		TextField numWordsArea = new TextField();
		TextField startingWordArea = new TextField();
		
		GridPane tGP = new GridPane();
		
		//adds items to gridpane
		tGP.add(wordAreaLabel, 0, 0);
		tGP.add(startingWordLabel, 0 , 1);
		
		tGP.add(numWordsArea, 1, 0);
		tGP.add(startingWordArea, 1, 1);
		
		tGP.add(pickForMeButton, 2, 1);
		
		tGP.add(genButton, 0, 2);
		tGP.add(cancelButton, 1, 2);
		
		tGP.setPadding(new Insets(20,20,20,20));
		tGP.setHgap(20);
		tGP.setVgap(20);
		
		cancelButton.setOnAction(e->{
			promptStage.close();
		});
		
		pickForMeButton.setOnAction(e->{ //this button picks a word
			startingWordArea.setText(textGen.getRandomKeyWord());//puts random starting word into text area
		});
		
		genButton.setOnAction(e->{
			@SuppressWarnings("unchecked")
			Task<String> genTask = new Task<String>(){
				@Override
				protected String call(){
					try{
					int numWords = Integer.parseInt(numWordsArea.getText());
					if (numWords < 1){ //so the user can't try to generate a negative amount of words
						Platform.runLater(() ->{//Schedules pop-up for execution, prevents it from being blocked
							lessThan0Alert.showAndWait();
						});
						return null;
					}
					String startingWord = startingWordArea.getText();
					return textGen.generateParagraph(numWords, startingWord);
					}catch(NumberFormatException e){
						//number is wrong
						Platform.runLater(() ->{//Schedules pop-up for execution, prevents it from being blocked
							nonNumericalAlert.showAndWait();
						});
					}catch(WordNotFoundException e){
						//invalid starting word error
						Platform.runLater(() ->{//Schedules pop-up for execution, prevents it from being blocked
							wordNotInDatabaseAlert.showAndWait();
						});
					}
					return null;//returns null string on error
				}
			};
			genTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, //done after textGen is built
			        new EventHandler<WorkerStateEvent>() {
			    @Override
			    public void handle(WorkerStateEvent t) {

			    	if(genTask.getValue() != null){ //if the task completed without giving an error
				    	textArea.setText(genTask.getValue()); //gets new initialized text editor
				    	promptStage.close();
			    	}
			    }
			});
			
			new Thread(genTask).start();
		});
		
		Scene scene2 = new Scene(tGP,550,200);
		//scene2.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		promptStage.setScene(scene2);
		promptStage.setTitle("Generate Text");
	}
	
	public void genCalendarStage(){ //builds prompt used to pick date
		
		StringConverter<LocalDate> converter = new StringConverter<LocalDate>() { //makes a converter that is used by the date picker object to allow for plain text input
            DateTimeFormatter dateFormatter = 
                DateTimeFormatter.ofPattern(StockDataBase.TIME_PATTERN); //gets the pattern from the stock object
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
		};
		
		datePicker1.setConverter(converter);//adds the converter
		datePicker1.setPromptText(StockDataBase.TIME_PATTERN.toLowerCase());//shows pattern in text area when its blank
		
		//initialize buttons, formating etc.
		Button cancelButton = new Button("Cancel");
		Button confirmButton = new Button("OK");
		HBox buttonBox = new HBox(30);
		VBox vbox = new VBox(20);
        vbox.setStyle("-fx-padding: 10;");
        buttonBox.setStyle("-fx-padding: 10;");
        Scene sceneCal = new Scene(vbox, 200, 125);
        calendarStage.setScene(sceneCal);
        
        buttonBox.getChildren().addAll(confirmButton, cancelButton);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Label checkInlabel = new Label("Stock Date:");
        gridPane.add(checkInlabel, 0, 0);

        GridPane.setHalignment(checkInlabel, HPos.LEFT);
        gridPane.add(datePicker1, 0, 1);
        gridPane.add(buttonBox, 0, 2);
        vbox.getChildren().add(gridPane);
        
        confirmButton.setOnAction(e->{
        	Double stockPrice = stockDB.getStockPrice((LocalDate)datePicker1.getValue());
        	DayOfWeek day = datePicker1.getValue().getDayOfWeek();
        	if(day.equals(DayOfWeek.SUNDAY) || day.equals(DayOfWeek.SATURDAY)){ //if the date picked is on the weekend shows an error
        		dateNotFoundAlert.setHeaderText("No stock data for weekends.");
        		dateNotFoundAlert.setContentText("The stock market is closed on the weekends.");
        		dateNotFoundAlert.showAndWait();
        	}else if(stockPrice == null){ //if the date picked is null and not on the weekend its out of range for the dates entered
        		dateNotFoundAlert.setHeaderText("Date picked out of range.");
        		dateNotFoundAlert.setContentText("Date must be between 2006-12-11 and 2016-12-09");
        		dateNotFoundAlert.showAndWait();
        	}else if(stockPrice == -1){ //-1 is used to store a date where there is no data, usually on holidays
        		dateNotFoundAlert.setHeaderText("No stock data for this day.");
        		dateNotFoundAlert.setContentText("The stock market was likely closed.");
        		dateNotFoundAlert.showAndWait();
        	}else{ //a correct data was entered with a stock that was found
        		//removes counts from the bottom bar
        		wordCountToggle.setSelected(false);
        		sentenceCountToggle.setSelected(false);
        		syllableCountToggle.setSelected(false);
        		fleschScoreToggle.setSelected(false);
        		
        		//resets label
        		stockLabel.setText("Stock Price: $" + stockPrice + "           Access Time: " 
        							+ stockDB.getAccessTime() + " ms");
        		
        		
        		infoBar.getChildren().clear();
        		infoBar.getChildren().add(stockLabel); //adds stock label
        		calendarStage.close(); //closes window
        	}
        });
        
        cancelButton.setOnAction(e->{
        	calendarStage.close(); //closes window
        });
        
	}
	
	public boolean allFilesAnalysed(){//checks to see if every parsed file has been analyzed
		for(int i = 0; i < parsedFilesAnalysed.length; i++){
			if(!parsedFilesAnalysed[i]){
				return false;
			}
		}
		return true;
	}
	
	public void importFile(File f){ //imports file
		
		for(int i = 0; i < 10; i++){//disables all parsed file imports temp.
			tfMenuItemArray[i].setDisable(true);
		}
		importFileMenuItem.setDisable(true);
		analyseTextItem.setDisable(true);

		Task<String> task = new Task<String>() { //new thread set up to not bog down gui thread
	    	@Override public String call() {
	    		StringBuilder sb = new StringBuilder(); // to be used to construct file
	    		try{
		    		BufferedReader br = new BufferedReader(new FileReader(f));
		    		String line; //stores current line
		    		while((line = br.readLine()) != null){//reads lines until it is null
		    			sb.append(line);//adds line to file
		    			sb.append("\n");//indents
		    		}
		    		br.close();
	    		}catch (IOException e) {
					e.printStackTrace();
				}finally{
					return sb.toString();//returns the string that was built upon completion
				}
	    	}
		};
		
		task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, //on task completion
		        new EventHandler<WorkerStateEvent>() {
		    @Override
		    public void handle(WorkerStateEvent t) {
		    	
		    	textArea.setText(task.getValue()); //sets text area value to string that was built in the thread
		    	
		    	for(int i = 0; i < 10; i++){//enables all file imports
					tfMenuItemArray[i].setDisable(false);
				}
				importFileMenuItem.setDisable(false);
				analyseTextItem.setDisable(false);
		    }
		});
		
		new Thread(task).start();//starts the thread
	}
	
	public void analyzeText(String s){
		
		if(s.equals("")){ //breaks out and displays error message if text area is blank
			textAreaBlankAlert.showAndWait();
			return;
		}
		
		//duplicated values to make compiler happy with multi-threading
		boolean tempB = false;
		int j = -1;
		
		for(int i = 0; i < 10; i++){
			if(curFile != null && curFile.getName().equals("parsedFile" + i + ".txt")){ //checks to see if file is a truncated file
				j = i;
				tempB = true;
				break;
			}
		}
		int numFile = j; //j is the number of the parsed file, the variable is copied over to work in a new thread
		boolean isAParsedFile = tempB;
		
		Task<Readability1> task1 = new Task<Readability1>() { //runs readability1 in new thread
	    	@Override public Readability1 call() {
	    		Readability1 r1 = new Readability1(s);
	    		r1.getFleschScore();
	    		return r1; //returns r1 from thread
	    	}
		};
		
		task1.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, 
		        new EventHandler<WorkerStateEvent>() {
		    @Override
		    public void handle(WorkerStateEvent t) {
		    	Readability1 r1 = task1.getValue(); //gets value from thread
		    	
		    	if(isAParsedFile){// if the file is a parsed file
					times1[numFile] = r1.getTimeToComplete(); //saves time to complete
					r1Array[numFile] = r1; //saves r1 for its counts
				}
		    }
		});
		
		Task<Readability2> task2 = new Task<Readability2>() { //runs readability2 in new thread
		    @Override public Readability2 call() {
		    	Readability2 r2 = new Readability2(s);
		        return r2; //returns r2 from thread
		    }
		};
		task2.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, 
		        new EventHandler<WorkerStateEvent>() {
		    @Override
		    public void handle(WorkerStateEvent t) {
		    	
		    	Readability2 r2 = task2.getValue();//gets r2 from thread
		    	//sets scores for gui
				numWords = r2.getNumWords();
				numSyllables =  r2.getNumSyllables();
				numSentences = r2.getNumSentences();
				fleschScore = r2.getFleschScore();
				
				if(isAParsedFile){ //if current file is a parsed file
					times2[numFile] = r2.getTimeToComplete();//saves time to complete
					parsedFilesAnalysed[numFile] = true;//sets corresponding value in array to true
					tfMenuItemArray[numFile].setGraphic(new Circle(5, Color.GREEN));//gives corresponding menu item a green circle
					r2Array[numFile] = r2; //saves object for counts
					if(allFilesAnalysed()){ //checks to see if every parsed file is analyzed
						analyseAllMenuItem.setGraphic(new Circle(5, Color.GREEN));//sets circle to green
					}
				}
				
				//updates display data / gui elements
				analyseTextItem.setGraphic(new Circle(5, Color.GREEN));
				changeCountsGreen();
				updateDisplayData();
				
				fileAnalysed = true;
		    }
		});
		if(isAParsedFile){ //will only run both readability objects if the file loaded is one of the truncated ones, decreases execution time
			new Thread(task1).start();
		}
		new Thread(task2).start();
	}
	
	public void parseText(String s){
		
		//clears and resets all related to parsed files
		
		times1 = new double[10];
		times2 = new double[10];
		
		r1Array = new Readability1[10];
		r2Array = new Readability2[10];
		
		parsedFilesAnalysed = new boolean[10];
		
		if(curFileIsParsedFile()){
			curFile = null;
		}
		
		if(s.equals("")){
			textAreaBlankAlert.showAndWait();
			return;
		}
		
		infoBar.getChildren().add(progressBar); //adds progress bar
		
		Task<Void> task = new Task<Void>() { //runs in new thread
		    @Override public Void call() {
				try {
					Queue<String> wordQueue = new ArrayDeque<String>(); //queue stores words as they are pulled
					int wordCount = 0; //used to count words
					Scanner scan = new Scanner(s); //scanner is passed the string that was passed to the method
					String word; //used to store current word
					
					while(scan.hasNext()){ //loops while scanner has another word
						word = scan.next(); //word is pulled from scanner
						wordQueue.add(word); //word is then added to queue
						wordCount++; //word count incremented
					}
					scan.close();
					
					int loopCount = 0; // counts how many words have been pulled from queue

					int oneTenthWords = (int)Math.round((double)wordCount/10.0);//calcs 1/10 total words
					StringBuilder sb = new StringBuilder(); //sb used to construct partial files
					
					while(loopCount < wordCount){ //stops when all words have been pulled

						String sNext = wordQueue.poll();//pulls word from queue
						sb.append(sNext + " "); //adds word to sb
						if(loopCount % 15 == 0 && loopCount != 0){ //adds a new line every 15 words, for formating
							sb.append("\n");
						}
						if(loopCount % oneTenthWords == 0  && loopCount != 0){ //if the current count is at (some int)/10
							updateProgress((int)Math.ceil((double)loopCount / (double)oneTenthWords) + 1, 10); // adds 10% to the loading bar
							
							//Immediately saves file
							BufferedWriter br = new BufferedWriter(new FileWriter("inputData/parsedFile" 
																+ ((int)Math.ceil((double)loopCount / (double)oneTenthWords)-1) + ".txt"));
							br.write(sb.toString());
							br.close();
						}
						loopCount++; //loop count is incremented
					}
					BufferedWriter br = new BufferedWriter(new FileWriter("inputData/parsedFile9.txt")); //saves final file
					br.write(sb.toString());
					br.close();
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null; //needs to return a null value
		    }
		};
		
			progressBar.progressProperty().bind(task.progressProperty());//binds the loading bar to other thread
			
			task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, 
			        new EventHandler<WorkerStateEvent>() {
			    @Override
			    public void handle(WorkerStateEvent t) {//updates gui
			    	setPartialFilesYellow();
			    	truncateMenuItem.setGraphic(new Circle(5, Color.GREEN));
			    	parsedFilesExists = true;
			    	progressBar.progressProperty().unbind(); //releases loading bar from other thread
			    	infoBar.getChildren().remove(progressBar);//removes loading bar
			    }
			});
			
			new Thread(task).start();
		
	}
	
	public boolean curFileIsParsedFile(){ //checks if the cur file loaded is one of the parsed files
		if(curFile == null){
			return false;
		}
		for(int i = 0; i < 10; i++){
			if(curFile.getName().equals("parsedFile" + i + ".txt")){
				return true;
			}
		}
		return false;
	}
	
	public void saveText(String text, File f){//saves file
		try {
			BufferedWriter bw = new BufferedWriter (new FileWriter(f));
			bw.write(text);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setPartialFilesRed(){ //adds red circle to partial file menu items
		analyseAllMenuItem.setGraphic(new Circle(5, Color.RED));
		for(int i = 0; i < 10; i++){
			tfMenuItemArray[i].setGraphic(new Circle(5, Color.RED));
		}
	}
	public void setPartialFilesYellow(){ //adds yellow circle to partial file menu items
		analyseAllMenuItem.setGraphic(new Circle(5,  Color.web("#ffd84f")));
		for(int i = 0; i < 10; i++){
			tfMenuItemArray[i].setGraphic(new Circle(5,  Color.web("#ffd84f")));
		}
	}
	public void changeCountsGreen(){//adds green circle to toggles
		fleschScoreToggle.setGraphic(new Circle(5, Color.GREEN));
		wordCountToggle.setGraphic(new Circle(5, Color.GREEN));
		syllableCountToggle.setGraphic(new Circle(5, Color.GREEN));
		sentenceCountToggle.setGraphic(new Circle(5, Color.GREEN));
	}
	public void changeCountsRed(){//toggles to false and adds red circle
		fleschScoreToggle.setGraphic(new Circle(5, Color.RED));
		wordCountToggle.setGraphic(new Circle(5, Color.RED));
		syllableCountToggle.setGraphic(new Circle(5, Color.RED));
		sentenceCountToggle.setGraphic(new Circle(5, Color.RED));
		
		wordCountToggle.setSelected(false);
		sentenceCountToggle.setSelected(false);
		syllableCountToggle.setSelected(false);
		fleschScoreToggle.setSelected(false);
	}
	
	public void updateDisplayData(){ //changes labels to new values
		fleschLabel.setText("Flesch Score: " + Math.round(fleschScore*10)/10.0);
		numWordsLabel.setText("Words: " + (int)numWords);
		numSyllablesLabel.setText("Syllables: " + (int)numSyllables);
		numSentencesLabel.setText("Sentences: " + (int)numSentences);
	}
	
	public void displayChart(Stage stage){ //shows time to complete data in line graph
		stage.setTitle("Algorithm Comparison");
        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Time in Millis to Complete");
        xAxis.setLabel("% Of Full File");
        //creating the chart
        final LineChart<Number,Number> lineChart = 
                new LineChart<Number,Number>(xAxis,yAxis);
                
        lineChart.setTitle("Time to Complete");
        //defining a series
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("3 While Loops");
        //populating the series with data
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("1 While Loop");
        for(int i = 1; i < 11; i++){ //only adds files that have been analyzed
        	if(parsedFilesAnalysed[i-1]){
	        	 series1.getData().add(new XYChart.Data(i*10, times1[i-1]));
	        	 series2.getData().add(new XYChart.Data(i*10, times2[i-1]));
        	}
        }
        
        Scene scene  = new Scene(lineChart,800,600);
        lineChart.getData().addAll(series1, series2);
       
        stage.setScene(scene);
        stage.show();
	}
}

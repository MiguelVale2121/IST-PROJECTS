// Bakeoff #2 - Seleção de Alvos Fora de Alcance
// IPM 2021-22, Período 3
// Entrega: até dia 22 de Abril às 23h59 através do Fenix
// Bake-off: durante os laboratórios da semana de 18 de Abril

// p5.js reference: https://p5js.org/reference/

// Database (CHANGE THESE!)
const GROUP_NUMBER   = "GROUP";      // Add your group number here as an integer (e.g., 2, 3)
const BAKE_OFF_DAY   = "false";  // Set to 'true' before sharing during the bake-off day

// Target and grid properties (DO NOT CHANGE!)
let PPI, PPCM;
let TARGET_SIZE;
let TARGET_PADDING, MARGIN, LEFT_PADDING, TOP_PADDING;
let continue_button;
let inputArea        = {x: 0, y: 0, h: 0, w: 0}    // Position and size

// Metrics
let testStartTime, testEndTime; // time between the start and end
let hits 			 = 0;      // number of successful selections
let misses 			 = 0;      // number of missed selections (used to calculate accuracy)
let database;                  // Firebase DB  

// Study control parameters
let draw_targets = false;  // used to control what to show in draw()
let trials = [];     //contains the order of targets
let current_trial = 0;      // the current trial number
let attempt = 0;      // users complete each test twice to account 
let fitts_IDs = [];     // add the Fitts ID for each selection here
let fittsIDValue = 0;      // value to be added to the array 
let fittsIDIndex = 0;      // Index to keep track of last added fitsID
let fittsIDAllow = false;  // Allows the calculation of the fittsIDAllow


// Target class (position and width)
class Target
{
  constructor(x, y, w)
  {
    this.x = x;
    this.y = y;
    this.w = w;
  }
}

// Runs once at the start
function setup()
{
  createCanvas(700, 500);    // window size in px before we go into fullScreen()
  frameRate(60);             // frame rate (DO NOT CHANGE!)
  
  randomizeTrials();         // randomize the trial order at the start of execution
  
  textFont("Arial", 18);     // font size for the majority of the text
  drawUserIDScreen();        // draws the user start-up screen (student ID and display size)
}

// Runs every frame and redraws the screen
function draw()
{
  if (draw_targets)
  {     
    // The user is interacting with the 6x3 target grid
    background(color(255,255,255));        // sets background to white
    
    // Print trial count at the top left-corner of the canvas
    fill(color(0,0,0));
    textAlign(LEFT);
    text("Trial " + (current_trial + 1) + " of " + trials.length, 50, 20);
    
    // Draw all 18 targets
	for (var i = 0; i < 18; i++) drawTarget(i);
    
    // Draw the user input area
    drawInputArea()

    // Draw the virtual cursor
    let x = map(mouseX, inputArea.x, inputArea.x + inputArea.w, 0, width)
    let y = map(mouseY, inputArea.y, inputArea.y + inputArea.h, 0, height)
    
    for (let i=0; i<18; ++i){
    target = getTargetBounds(i);
    
      if(dist(x,y,target.x,target.y)< 1.5*PPCM ) {
        x = target.x;
        y = target.y;
        break;
      }
    }
    fill(color(0,0,0));
    circle(x, y, 0.5 * PPCM);
    
  }
}

// Print and save results at the end of 54 trials
function printAndSavePerformance()
{
  // DO NOT CHANGE THESE! 
  let accuracy			= parseFloat(hits * 100) / parseFloat(hits + misses);
  let test_time         = (testEndTime - testStartTime) / 1000;
  let time_per_target   = nf((test_time) / parseFloat(hits + misses), 0, 3);
  let penalty           = constrain((((parseFloat(95) - (parseFloat(hits * 100) / parseFloat(hits + misses))) * 0.2)), 0, 100);
  let target_w_penalty	= nf(((test_time) / parseFloat(hits + misses) + penalty), 0, 3);
  let timestamp         = day() + "/" + month() + "/" + year() + "  " + hour() + ":" + minute() + ":" + second();
  
  background(color(0,0,0));   // clears screen
  fill(color(255,255,255));   // set text fill color to white
  text(timestamp, 10, 20);    // display time on screen (top-left corner)
  
  textAlign(CENTER);
  text("Attempt " + (attempt + 1) + " out of 2 completed!", width/2, 60); 
  text("Hits: " + hits, width/2, 100);
  text("Misses: " + misses, width/2, 120);
  text("Accuracy: " + accuracy + "%", width/2, 140);
  text("Total time taken: " + test_time + "s", width/2, 160);
  text("Average time per target: " + time_per_target + "s", width/2, 180);
  text("Average time for each target (+ penalty): " + target_w_penalty + "s", width/2, 220);
  
  // Print Fitts IDS (one per target, -1 if failed selection, optional)
  // 
  text("Fitts Index of Performance", width/2, 260);
  
  for (var i = 0; i < fitts_IDs.length/2; i++) {
    if (fitts_IDs[i] == -1)
      text("Target " + (i+1) + ": MISSED", (width/2)-200, (290 + 20*i));
    else
      text("Target " + (i+1) + ": " + fitts_IDs[i], (width/2)-200, (290 + 20*i));     
  }
  
  let k = 0;
  
  for (var j = fitts_IDs.length/2; j < fitts_IDs.length; j++) {    
    if (fitts_IDs[j] == -1)
      text("Target " + (j+1) + ": MISSED", (width/2)+200, (290 + 20*k));
    else
      text("Target " + (j+1) + ": " + fitts_IDs[j], (width/2)+200, (290 + 20*k));    
    k++;
  }

  // Saves results (DO NOT CHANGE!)
  let attempt_data = 
  {
        project_from:       GROUP_NUMBER,
        assessed_by:        student_ID,
        test_completed_by:  timestamp,
        attempt:            attempt,
        hits:               hits,
        misses:             misses,
        accuracy:           accuracy,
        attempt_duration:   test_time,
        time_per_target:    time_per_target,
        target_w_penalty:   target_w_penalty,
        fitts_IDs:          fitts_IDs
  }
  
  // Send data to DB (DO NOT CHANGE!)
  if (BAKE_OFF_DAY)
  {
    // Access the Firebase DB
    if (attempt === 0)
    {
      firebase.initializeApp(firebaseConfig);
      database = firebase.database();
    }
    
    // Add user performance results
    let db_ref = database.ref('G' + GROUP_NUMBER);
    db_ref.push(attempt_data);
  }
}

// Mouse button was pressed - lets test to see if hit was in the correct target
function mousePressed() 
{
  // Only look for mouse releases during the actual test
  // (i.e., during target selections)
  if (draw_targets)
  {
    // Get the location and size of the target the user should be trying to select
    let target = getTargetBounds(trials[current_trial]);   
    
    // Check to see if the virtual cursor is inside the target bounds,
    // increasing either the 'hits' or 'misses' counters
        
    if (insideInputArea(mouseX, mouseY))
    {
      let virtual_x = map(mouseX, inputArea.x, inputArea.x + inputArea.w, 0, width)
      let virtual_y = map(mouseY, inputArea.y, inputArea.y + inputArea.h, 0, height)

      if (dist(target.x, target.y, virtual_x, virtual_y) < 1.5*PPCM){
        hits++;
        fitts_IDs[fittsIDIndex] = fittsIDValue;
        fittsIDIndex++;
        fittsIDAllow = true;
      }
                           
      else{
        misses++;
        fitts_IDs[fittsIDIndex] = -1;
        fittsIDIndex++;
        fittsIDAllow = true;
      }
      current_trial++;                 // Move on to the next trial/target
    }
        
    // Check if the user has completed all 54 trials
    if (current_trial === trials.length)
    {
      testEndTime = millis();
      draw_targets = false;          // Stop showing targets and the user performance results
      printAndSavePerformance();     // Print the user's results on-screen and send these to the DB
      attempt++;                      
      
      // If there's an attempt to go create a button to start this
      if (attempt < 2)
      {
        continue_button = createButton('START 2ND ATTEMPT');
        continue_button.mouseReleased(continueTest);
        continue_button.position(width/2 - continue_button.size().width/2, height/2 - continue_button.size().height/2);
      }
    }
    else if (current_trial === 1) testStartTime = millis();
  }
}

// Draw target on-screen
function drawTarget(i)
{
  // Get the location and size for target (i)
  let target = getTargetBounds(i); 
  let virtual_x = map(mouseX, inputArea.x, inputArea.x + inputArea.w, 0, width)
  let virtual_y = map(mouseY, inputArea.y, inputArea.y + inputArea.h, 0, height)

  //Draw Line between current and next Targets
  if (current_trial < trials.length) {
    stroke(color(0,0,255));
    line(getTargetBounds(trials[current_trial]).x, getTargetBounds(trials[current_trial]).y, getTargetBounds(trials[current_trial+1]).x, getTargetBounds(trials[current_trial+1]).y);
  }
  
  // Check whether this target is the target the user should be trying to select
  if (trials[current_trial] === i)  { 
    
    //Calculate fittsIDValue
    if (fittsIDAllow) {
      fittsIDValue = round(Math.log2((dist(target.x, target.y, mouseX, mouseY)/TARGET_SIZE)+1), 3);
      fittsIDAllow = false;
    }
    // Highlights the target the user should be trying to select
    // with a black border
    if(trials[current_trial] === trials[current_trial+1]) {        
      stroke(color(252,0,0));
    }      
    else if (dist(target.x, target.y, virtual_x, virtual_y) < 1.4*PPCM) {
      stroke(color(31,69,252))
    } else {
      stroke(color(0,0,0));
    }
      
    
    strokeWeight(4);
    // Remember you are allowed to access targets (i-1) and (i+1)
    // if this is the target the user should be trying to select
    //
    targetColor = color(0,255,0);
    targetColor.setAlpha(128 + 128 * (sin(millis() / 50)+ 0.5));
    fill(targetColor);                 
    circle(target.x, target.y, target.w);
    
  }
  
  else if (trials[current_trial + 1] === i){
    stroke(color(155,155,155));
    strokeWeight(1);
    
    fill(color(240,0,0));                 
    circle(target.x, target.y, target.w);
  }
  // Does not draw a border if this is not the target the user
  // should be trying to select         

  else{
    
  noStroke();

  // Draws the target
  fill(color(155,155,155));                 
  circle(target.x, target.y, target.w);
  }
}

// Returns the location and size of a given target
function getTargetBounds(i)
{
  var x = parseInt(LEFT_PADDING) + parseInt((i % 3) * (TARGET_SIZE + TARGET_PADDING) + MARGIN);
  var y = parseInt(TOP_PADDING) + parseInt(Math.floor(i / 3) * (TARGET_SIZE + TARGET_PADDING) + MARGIN);

  return new Target(x, y, TARGET_SIZE);
}

// Evoked after the user starts its second (and last) attempt
function continueTest()
{
  // Re-randomize the trial order
  shuffle(trials, true);
  current_trial = 0;
  print("trial order: " + trials);
  
  // Resets performance variables
  hits = 0;
  misses = 0;
  fitts_IDs = [];
  fittsIDIndex = 0;
  fittsIDValue = 0;
  
  
  continue_button.remove();
  
  // Shows the targets again
  draw_targets = true;
  testStartTime = millis();  
}

// Is invoked when the canvas is resized (e.g., when we go fullscreen)
function windowResized() 
{
  resizeCanvas(windowWidth, windowHeight);
    
  let display    = new Display({ diagonal: display_size }, window.screen);

  // DO NOT CHANGE THESE!
  PPI            = display.ppi;                        // calculates pixels per inch
  PPCM           = PPI / 2.54;                         // calculates pixels per cm
  TARGET_SIZE    = 1.5 * PPCM;                         // sets the target size in cm, i.e, 1.5cm
  TARGET_PADDING = 1.5 * PPCM;                         // sets the padding around the targets in cm
  MARGIN         = 1.5 * PPCM;                         // sets the margin around the targets in cm

  // Sets the margin of the grid of targets to the left of the canvas (DO NOT CHANGE!)
  LEFT_PADDING   = width/3 - TARGET_SIZE - 1.5 * TARGET_PADDING - 1.5 * MARGIN;        

  // Sets the margin of the grid of targets to the top of the canvas (DO NOT CHANGE!)
  TOP_PADDING    = height/2 - TARGET_SIZE - 3.5 * TARGET_PADDING - 1.5 * MARGIN;
  
  // Defines the user input area (DO NOT CHANGE!)
  inputArea      = {x: width/2 + 2 * TARGET_SIZE,
                    y: height/2,
                    w: width/3,
                    h: height/3
                   }

  // Starts drawing targets immediately after we go fullscreen
  draw_targets = true;
  
  // Allows the calculation of the first fittsIDValue
  fittsIDAllow = true;
}

function drawTargetInsideInputArea(i){
  target = getTargetBounds(i);

  x = map(target.x, 0, width, inputArea.x, inputArea.x + inputArea.w);
  y = map(target.y, 0, height, inputArea.y, inputArea.y + inputArea.h);
  let virtual_x = map(mouseX, inputArea.x, inputArea.x + inputArea.w, 0, width)
  let virtual_y = map(mouseY, inputArea.y, inputArea.y + inputArea.h, 0, height)
  size = target.w * (inputArea.w / height);
  
  noStroke();
  fill(color(155,155,155));
  

  if (trials[current_trial] === i) {
    fill((color(0,255,0)));
    
    if(trials[current_trial] === trials[current_trial+1]) {        
      stroke(color(252,0,0));
    }      
    else if (dist(target.x, target.y, virtual_x, virtual_y) < 1.4*PPCM) {
      stroke(color(31,69,252))
    } else {
      stroke(color(0,0,0));
    }

    if (trials[current_trial + 1] === i) {
      strokeWeight(4);
      stroke(color(0,0,0));
    }
  }
  else if (trials[current_trial + 1] === i) {
    fill(color(240,0,0));
  }

  circle(x, y, size);
}
// Responsible for drawing the input area
function drawInputArea()
{
  noFill();
  stroke(color(0,0,0));
  strokeWeight(2);
  
  rect(inputArea.x, inputArea.y, inputArea.w, inputArea.h);

  for (let i = 0; i < 18; i++) {
    drawTargetInsideInputArea(i);
  }
}
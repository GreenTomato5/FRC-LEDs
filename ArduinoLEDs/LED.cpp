// Define pins
const int pwmInputPin = A0;    // Analog input pin for PWM signal
const int redPin = 9;          // RGB LED red pin (PWM capable)
const int greenPin = 8;        // RGB LED green pin (PWM capable)
const int bluePin = 7;         // RGB LED blue pin (PWM capable)

// Structure to define LED states
struct LedState {
  int stateId;        // State identifier (matches incoming PWM value)
  int redValue;       // 0-255 for red LED
  int greenValue;     // 0-255 for green LED
  int blueValue;      // 0-255 for blue LED
  const char* name;   // State name for debugging
};

const LedState LED_STATES[] = {
  {0, 255, 0, 0, "Red"},      // Pure red
  {10, 255, 128, 0, "Orange"},// Orange
  {20, 255, 255, 0, "Yellow"},// Yellow 
  {30, 0, 255, 0, "Green"},   // Pure green
  {40, 0, 255, 255, "Cyan"},  // Cyan 
  {50, 0, 0, 255, "Blue"},    // Pure blue
  {60, 128, 0, 255, "Purple"} // Purple
};

const int NUM_STATES = sizeof(LED_STATES) / sizeof(LED_STATES[0]);
int currentState = -1;          // Current state (-1 means no valid state yet)
int lastUnknownState = -1;      // Cache for the last unknown state
unsigned long pulseWidth = 0;
const int STATE_TOLERANCE = 4;  // TODO: Bro this is like not very many times like its kinda low idk

void setup() {
  // Configure pins
  pinMode(pwmInputPin, INPUT);
  pinMode(redPin, OUTPUT);
  pinMode(greenPin, OUTPUT);
  pinMode(bluePin, OUTPUT);
  
  // Initialize serial for debugging
  Serial.begin(9600);
  Serial.println("RGB LED Controller Started");
  Serial.println("Available States:");
  for (int i = 0; i < NUM_STATES; i++) {
    Serial.print("State at ");
    Serial.print(LED_STATES[i].stateId);
    Serial.print("µs: ");
    Serial.println(LED_STATES[i].name);
  }
}

// Optimized state finding function with caching
int findState(unsigned long pulseWidth) {
  // First check if this is the same unknown state as before
  if (lastUnknownState != -1) {
    int expectedPulse = LED_STATES[lastUnknownState].stateId;
    if (abs((int)pulseWidth - expectedPulse) < STATE_TOLERANCE) {
      return lastUnknownState;
    }
  }
  
  // If not cached or cache miss, search through states
  for (int i = 0; i < NUM_STATES; i++) {
    if (abs((int)pulseWidth - LED_STATES[i].stateId) < STATE_TOLERANCE) {
      lastUnknownState = i;  // Cache the found state
      return i;
    }
  }
  
  lastUnknownState = -1;  // Reset cache if no state found
  return -1;
}

void setLedColor(const LedState& state) {
  analogWrite(redPin, state.redValue);
  analogWrite(greenPin, state.greenValue);
  analogWrite(bluePin, state.blueValue);
}

void loop() {
  // Read the pulse width
  pulseWidth = pulseIn(pwmInputPin, HIGH);
  
  // Find matching state
  int newState = findState(pulseWidth);
  
  // Update LED if state changed
  if (newState != -1 && newState != currentState) {
    currentState = newState;
    setLedColor(LED_STATES[currentState]);
    
    // Debug output
    Serial.print("Pulse Width: ");
    Serial.print(pulseWidth);
    Serial.print(" µs | State: ");
    Serial.print(LED_STATES[currentState].stateId);
    Serial.print(" (");
    Serial.print(LED_STATES[currentState].name);
    Serial.print(") | RGB: ");
    Serial.print(LED_STATES[currentState].redValue);
    Serial.print(",");
    Serial.print(LED_STATES[currentState].greenValue);
    Serial.print(",");
    Serial.println(LED_STATES[currentState].blueValue);
  }
  
  delay(20);
}
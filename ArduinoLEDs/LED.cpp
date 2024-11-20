#include <Adafruit_NeoPixel.h>

#define PIN 6            // Data pin to first LED
#define NUM_LEDS 300      // Number of LEDs in the chain (fake)

Adafruit_NeoPixel strip(NUM_LEDS, PIN, NEO_GRB + NEO_KHZ800);

unsigned long pulseWidth = 0;  // Store the pulse width from A0

// Structure to define states based on PWM input
struct LedState {
  unsigned long minPulseWidth; // Minimum pulse width for this state
  unsigned long maxPulseWidth; // Maximum pulse width for this state
  uint32_t color;             // RGB color value (use Adafruit's RGB format)
  const char* name;           // Name of the state for debugging
};

// Define the possible states based on pulse width ranges
const LedState LED_STATES[] = {
  {0, 1024, strip.Color(255, 0, 0), "Red"},      // Pulse width 0-1024 => Red
  {1025, 2048, strip.Color(0, 255, 0), "Green"}, // Pulse width 1025-2048 => Green
  {2049, 3072, strip.Color(0, 0, 255), "Blue"},  // Pulse width 2049-3072 => Blue
  {3073, 4096, strip.Color(255, 255, 255), "White"} // Pulse width 3073-4096 => White
};

const int NUM_STATES = sizeof(LED_STATES) / sizeof(LED_STATES[0]);

void setup() {
  strip.begin(); // Initialize the LED strip
  strip.show();  // Initialize all LEDs to 'off'
  
  pinMode(A0, INPUT);  // Set A0 as input for PWM signal
  
  // Initialize serial for debugging
  Serial.begin(9600);
  Serial.println("RGB LED Controller Started");
}

void loop() {
  // Read the PWM pulse width from A0
  pulseWidth = pulseIn(A0, HIGH);
  
  // Find the corresponding color state based on the pulse width
  for (int i = 0; i < NUM_STATES; i++) {
    if (pulseWidth >= LED_STATES[i].minPulseWidth && pulseWidth <= LED_STATES[i].maxPulseWidth) {
      // Set all LEDs to the corresponding color for this state
      setLedColor(LED_STATES[i].color);
      
      // Debug output
      Serial.print("Pulse Width: ");
      Serial.print(pulseWidth);
      Serial.print(" µs | State: ");
      Serial.print(LED_STATES[i].name);
      Serial.print(" | Color: ");
      Serial.println(LED_STATES[i].color, HEX);
      
      break;
    }
  }
  
  delay(20); 
}

void setLedColor(uint32_t color) {
  for (int i = 0; i < NUM_LEDS; i++) {
    strip.setPixelColor(i, color);  // Set the color for each LED
  }
  strip.show();  // Update the LEDs with the new color data
}

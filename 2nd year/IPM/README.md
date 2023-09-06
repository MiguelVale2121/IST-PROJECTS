# Human-Computer Interaction Project

## Introduction

This project is part of the Human-Computer Interaction subject and aims to reduce the target selection time for out-of-reach targets in an abstract interface. We provide a p5.js source code that:

- Displays a 6x3 grid of targets to your users.
- Indicates the target to select and the user's input area.
- Quantifies user performance based on accuracy (0-100%), total task time (seconds), average time per target (seconds), and average time per target with a penalty if the user's success rate is below 95% (seconds) (Figure 2).
- Stores these performance metrics on the Firebase platform.
- Recognizes user input in a restricted screen area and generates a virtual cursor in response to this input.

The challenge for this bake-off is to modify the provided source code so that your users can select targets as quickly as possible while keeping an eye on penalties for success rates below 95%. Additionally, you need to calculate and display an additional metric: the Fitts Index of Performance (ID). Please use the formula proposed by Mackenzie: `log2 (distance-to-target from the last selection / target-width + 1)` (refer to the theoretical class on "Human Factors" and chapter 2.1.4 on "The Movement"). Store each ID in Firebase and optionally display them at the end of the task (Figure 2). Calculate the ID using the virtual cursor, not the user's actual cursor.

## Grade
- **Grade:** 20

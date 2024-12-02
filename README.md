# StudentGo <img src="https://github.com/user-attachments/assets/51899b72-ecf0-42ba-8ecf-a64fd05e3623" width="50" align="center" style="border-radius: 25px;"/>

A gamified campus exploration app that encourages physical activity among college students through point-based location visits and friendly competition.

## Purpose

StudentGo transforms daily campus navigation into an engaging experience by:
- Awarding GO Points for visiting campus locations
- Creating friendly competition through weekly leaderboards
- Encouraging campus exploration and physical activity
- Building community through shared experiences

## Technical Architecture

### Frontend
- **UI Framework**: Native Android with Material Design 3
- **Navigation**: Single-activity architecture with Navigation Component
- **Animations**: Material motion system for smooth transitions
- **Maps Integration**: Google Maps Platform API for location services

### Backend
- **Authentication**: Firebase Authentication
- **Database**: Cloud Firestore
  - Collections:
    - users (scores, profiles)
    - locations (visit counts, coordinates)
    - leaderboard (weekly rankings)

### Key Features

#### Location-Based Point System
- Dynamic point allocation based on location popularity
- Real-time visit tracking
- Instant score updates

#### Weekly Competition
- Automated weekly score resets
- Persistent leaderboard tracking
- Fair competition cycles

#### Popular Locations
- Real-time popularity tracking
- Dynamic point values
- Visit statistics

## Data Flow
1. User visits location → GPS verification
2. Points awarded based on location ranking
3. Score updated in user profile
4. Leaderboard updated in real-time
5. Weekly reset maintains engagement

## Technologies Used
- Kotlin
- Firebase (Auth, Firestore)
- Google Maps Platform
- Material Design Components
- Android Jetpack Libraries

## Screenshots and Screen Recording

### Screenshots

<p float="left">
  <img src="https://github.com/user-attachments/assets/fdbb3284-26e9-4f98-b7a5-869bd50427b8" width="250" hspace="10"/>
  <img src="https://github.com/user-attachments/assets/fe161e47-9594-4c68-8cdd-79f4b4240481" width="250" hspace="10"/>
  <img src="https://github.com/user-attachments/assets/7499ab91-bf1f-46e2-87de-913fe12181ef" width="250" hspace="10"/>
</p>

<p float="left">
  <img src="https://github.com/user-attachments/assets/4f762517-a80a-4ef6-8621-660bdae3f32d" width="250" hspace="10"/>
  <img src="https://github.com/user-attachments/assets/cc2db447-d7cf-424d-8f9c-c48901c97a68" width="250" hspace="10"/>
  <img src="https://github.com/user-attachments/assets/968a3a18-cad6-4566-bb62-5ae47dea021a" width="250" hspace="10"/>
</p>

<p float="left">
  <img src="https://github.com/user-attachments/assets/c813cf6d-709f-46a5-aa90-21ab5b9a751b" width="250" hspace="10"/>
  <img src="https://github.com/user-attachments/assets/3b836466-a57a-418e-9020-82a282a7f970" width="250" hspace="10"/>
  <img src="https://github.com/user-attachments/assets/bd53d056-1d67-43f3-96e6-2be25ac5771a" width="250" hspace="10"/>
</p>

<p float="left">
  <img src="https://github.com/user-attachments/assets/de189562-4448-46c5-9dd6-517a345ea000" width="250" hspace="10"/>
</p>

### Screen Recording

https://github.com/user-attachments/assets/15952e6d-8eda-48ee-bce1-b83c773d9875


## Future Enhancements
- Achievement system
- Social features
- Custom location challenges
- Expanded statistics tracking

## References
[1] Physical activity statistics in college students  
[2] Transition impact on student health  
[3] Exercise benefits for student wellbeing  
[4] StudentGo development documentation  
[5] Google Maps Platform documentation  
[6] Firebase documentation

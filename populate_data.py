import requests
import json
from datetime import datetime, timedelta
import time

BASE_URL = "http://localhost:8080"
ADMIN_EMAIL = "simply23hemant@gmail.com"
ADMIN_PASS = "admin123"

def get_token():
    url = f"{BASE_URL}/api/auth/login/pass"
    payload = {"email": ADMIN_EMAIL, "password": ADMIN_PASS}
    response = requests.post(url, json=payload)
    if response.status_code == 200:
        return response.json().get("jwt")
    else:
        print(f"Login failed: {response.text}")
        return None

token = get_token()
if not token:
    exit(1)

headers = {
    "Authorization": f"Bearer {token}",
    "Content-Type": "application/json"
}

def create_city(name, state, country):
    url = f"{BASE_URL}/api/cities/create"
    payload = {"name": name, "state": state, "country": country, "isActive": True}
    res = requests.post(url, json=payload, headers=headers)
    if res.status_code == 200:
        print(f"City {name} created successfully.")
    else:
        print(f"Failed to create city {name}: {res.text}")

def create_genre(name):
    url = f"{BASE_URL}/api/genres/create"
    payload = {"name": name}
    res = requests.post(url, json=payload, headers=headers)
    if res.status_code == 200:
        print(f"Genre {name} created successfully.")
    else:
        print(f"Failed to create genre {name}: {res.text}")

def create_language(name):
    url = f"{BASE_URL}/api/languages/create"
    payload = {"name": name}
    res = requests.post(url, json=payload, headers=headers)
    if res.status_code == 200:
        print(f"Language {name} created successfully.")
    else:
        print(f"Failed to create language {name}: {res.text}")

# Setup Base Data
cities = [
    ("Delhi NCR", "Delhi", "India"),
    ("Bangalore", "Karnataka", "India"),
    ("Indore", "Madhya Pradesh", "India")
]
for c in cities: create_city(*c)

genres = ["Action", "Drama", "Comedy", "Sci-Fi", "Thriller", "Horror", "Romance", "Animation", "Adventure", "Crime"]
for g in genres: create_genre(g)

languages = ["Hindi", "English", "Tamil", "Telugu", "Kannada"]
for l in languages: create_language(l)

# Fetch IDs
genre_map = {g['name']: g['id'] for g in requests.get(f"{BASE_URL}/api/genres/all", headers=headers).json()}
lang_map = {l['name']: l['id'] for l in requests.get(f"{BASE_URL}/api/languages/all", headers=headers).json()}
city_map = {c['name']: c['id'] for c in requests.get(f"{BASE_URL}/api/cities/all", headers=headers).json()}

# Movies
upcoming_movies = [
    "Avengers: Secret Wars", "Spider-Man 4", "The Batman Part II", "Dune: Part Three", "Deadpool 4",
    "Mission: Impossible – Dead Reckoning Part Two", "Fast X: Part 2", "Joker: Folie à Deux",
    "Fantastic Four", "Gladiator 2", "War 2", "Tiger vs Pathaan", "Brahmastra Part 2", "Stree 3", "Hera Pheri 3"
]

now_showing_movies = [
    "Oppenheimer", "Barbie", "John Wick: Chapter 4", "The Batman", 
    "Doctor Strange in the Multiverse of Madness", "Avengers: Endgame", 
    "Inception", "Interstellar", "The Dark Knight", "Fight Club", 
    "The Shawshank Redemption", "Forrest Gump", "The Matrix", 
    "Gladiator", "Titanic", "Jawan", "Pathaan", "Animal", "Dunki", 
    "3 Idiots", "Zindagi Na Milegi Dobara", "Yeh Jawaani Hai Deewani", 
    "Drishyam 2", "Lagaan", "Gully Boy"
]

def add_movie(title, is_upcoming=False):
    url = f"{BASE_URL}/api/movies/create"
    release_date = (datetime.now() + timedelta(days=200 if is_upcoming else -100)).strftime("%Y-%m-%d")
    payload = {
        "title": title,
        "description": f"Detailed description for {title}. A must watch blockbuster.",
        "durationMinutes": 150,
        "releaseDate": release_date,
        "rating": "UA",
        "isActive": True,
        "genreIds": [genre_map["Action"], genre_map["Drama"]],
        "languageIds": [lang_map["Hindi"], lang_map["English"]],
        "posterUrl": f"https://image.tmdb.org/t/p/w500/placeholder.jpg",
        "bannerUrl": f"https://image.tmdb.org/t/p/original/placeholder.jpg",
        "trailerUrl": "https://www.youtube.com/embed/placeholder"
    }
    res = requests.post(url, json=payload, headers=headers)
    if res.status_code == 200:
        print(f"Movie {title} added.")
        return res.json().get("id")
    else:
        print(f"Failed to add movie {title}: {res.text}")
        return None

movie_ids = []
for m in now_showing_movies:
    mid = add_movie(m)
    if mid: movie_ids.append(mid)
for m in upcoming_movies:
    add_movie(m, is_upcoming=True)

# Theaters
theater_data = [
    ("PVR Select Citywalk", "Delhi NCR", "Saket, New Delhi"),
    ("Inox Nehru Place", "Delhi NCR", "Nehru Place, New Delhi"),
    ("IMAX DLF Mall of India", "Delhi NCR", "Noida, UP"),
    ("PVR Forum Mall", "Bangalore", "Koramangala, Bangalore"),
    ("Inox Garuda Mall", "Bangalore", "Magrath Road, Bangalore"),
    ("PVR C21 Mall", "Indore", "AB Road, Indore"),
    ("Inox Treasure Island", "Indore", "MG Road, Indore")
]

def add_theater(name, city_name, address):
    url = f"{BASE_URL}/api/theatre/owner/create"
    payload = {
        "name": name,
        "cityId": city_map[city_name],
        "address": address,
        "phone": "9876543210",
        "email": f"{name.lower().replace(' ', '')}@example.com",
        "amenities": ["Parking", "Cafe", "IMAX"],
        "foodBeverageAvailable": True,
        "openingHour": "09:00:00",
        "closingHour": "23:59:00"
    }
    res = requests.post(url, json=payload, headers=headers)
    if res.status_code == 200:
        print(f"Theater {name} added.")
        return res.json().get("id")
    else:
        print(f"Failed to add theater {name}: {res.text}")
        return None

theater_ids = []
for t in theater_data:
    tid = add_theater(*t)
    if tid: theater_ids.append(tid)

# Screens
screen_ids = []
for tid in theater_ids:
    for i in range(1, 4):
        url = f"{BASE_URL}/api/screens/create"
        payload = {
            "name": f"Screen {i}",
            "theatreId": tid,
            "screenType": "REGULAR" if i < 3 else "IMAX" if i == 3 else "DOLBY_ATMOS",
            "totalSeats": 150,
            "isActive": True
        }
        res = requests.post(url, json=payload, headers=headers)
        if res.status_code == 200:
            screen_ids.append(res.json().get("id"))
        else:
            print(f"Failed to add screen {i} for theater {tid}: {res.text}")

# Shows (for now showing movies)
def add_show(movie_id, screen_id, date, time_str):
    url = f"{BASE_URL}/api/show/create"
    payload = {
        "movieId": movie_id,
        "screenId": screen_id,
        "showDate": date,
        "showTime": time_str,
        "language": "Hindi",
        "seatPrices": {
            "GOLD": 450.0,
            "SILVER": 250.0,
            "BRONZE": 150.0
        }
    }
    res = requests.post(url, json=payload, headers=headers)
    if res.status_code == 200:
        pass # print(f"Show added for movie {movie_id} on screen {screen_id}")
    else:
        print(f"Failed to add show: {res.text}")

today = datetime.now().strftime("%Y-%m-%d")
for mid in movie_ids[:10]: # Limit shows for efficiency
    for sid in screen_ids[:10]:
        add_show(mid, sid, today, "14:00:00")
        add_show(mid, sid, today, "21:00:00")

print("Data Population Completed!")

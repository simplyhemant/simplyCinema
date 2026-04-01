import requests
import json

BASE_URL = "http://localhost:8080"
ADMIN_EMAIL = "simply23hemant@gmail.com"
ADMIN_PASS = "admin123"

def get_token():
    url = f"{BASE_URL}/api/auth/login/pass"
    payload = {"email": ADMIN_EMAIL, "password": ADMIN_PASS}
    response = requests.post(url, json=payload)
    if response.status_code == 200:
        return response.json().get("jwt")
    return None

token = get_token()
headers = {"Authorization": f"Bearer {token}", "Content-Type": "application/json"}

# Movie Metadata Mapping (Sample of real-ish data)
movie_metadata = {
    "Oppenheimer": {
        "poster": "https://image.tmdb.org/t/p/w500/8Gxv0sYWebbX6h209llimCU17du.jpg",
        "banner": "https://image.tmdb.org/t/p/original/r7D7YycMtmcSj0XvWCHo9pXqSZb.jpg",
        "desc": "The story of American scientist J. Robert Oppenheimer and his role in the development of the atomic bomb."
    },
    "Barbie": {
        "poster": "https://image.tmdb.org/t/p/w500/iuFNm7pS5R3Yv9qvY7LpTVqS7pB.jpg",
        "banner": "https://image.tmdb.org/t/p/original/ctM8r2oqbLTVuBrS9RZoelSRo3B.jpg",
        "desc": "Barbie and Ken are having the time of their lives in the colorful and seemingly perfect world of Barbie Land."
    },
    "John Wick: Chapter 4": {
        "poster": "https://image.tmdb.org/t/p/w500/vZloY0Cc8p4MvstkyuP4y1ZmtvL.jpg",
        "banner": "https://image.tmdb.org/t/p/original/h8GvS90z0o9id9zVpS9RZoelSRo3B.jpg",
        "desc": "John Wick uncovers a path to defeating The High Table. But before he can earn his freedom, Wick must face off against a new enemy."
    },
    "The Batman": {
        "poster": "https://image.tmdb.org/t/p/w500/74xTEgt7R36Fpooo50r9T6R9uEV.jpg",
        "banner": "https://image.tmdb.org/t/p/original/5P8mNndTjRPFpS9RZoelSRo3B.jpg",
        "desc": "In his second year of fighting crime, Batman explores the corruption that exists in Gotham City."
    },
    "Avengers: Endgame": {
        "poster": "https://image.tmdb.org/t/p/w500/or06vS3nB0GjI9Zp3p3p3p3p3p3.jpg",
        "banner": "https://image.tmdb.org/t/p/original/7RyB7pS5R3Yv9qvY7LpTVqS7pB.jpg",
        "desc": "After the devastating events of Infinity War, the universe is in ruins. With the help of remaining allies, the Avengers assemble once more."
    },
    "Interstellar": {
        "poster": "https://image.tmdb.org/t/p/w500/gEU2QniE6EwfVDxCzs25asSSTr7.jpg",
        "banner": "https://image.tmdb.org/t/p/original/rAiY_pS5R3Yv9qvY7LpTVqS7pB.jpg",
        "desc": "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival."
    },
    "The Dark Knight": {
        "poster": "https://image.tmdb.org/t/p/w500/qJ2tW6WMUDp9QEQvTqc6tFAufoO.jpg",
        "banner": "https://image.tmdb.org/t/p/original/nMKdURuuhI1vN3pS9RZoelSRo3B.jpg",
        "desc": "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests."
    },
    "Inception": {
        "poster": "https://image.tmdb.org/t/p/w500/edv5CZvR0rEk4uWv6M9S0Urdiy6.jpg",
        "banner": "https://image.tmdb.org/t/p/original/8Gxv0sYWebbX6h209llimCU17du.jpg",
        "desc": "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O."
    },
    "Jawan": {
        "poster": "https://image.tmdb.org/t/p/w500/jYmZ6R3Yv9qvY7LpTVqS7pB.jpg",
        "banner": "https://image.tmdb.org/t/p/original/r7D7YycMtmcSj0XvWCHo9pXqSZb.jpg",
        "desc": "A high-octane action thriller which outlines the emotional journey of a man who is set to rectify the wrongs in the society."
    },
    "Animal": {
        "poster": "https://image.tmdb.org/t/p/w500/v9qvY7LpTVqS7pB.jpg",
        "banner": "https://image.tmdb.org/t/p/original/ctM8r2oqbLTVuBrS9RZoelSRo3B.jpg",
        "desc": "The hardened son of a powerful industrialist billionaire returns home after years abroad and vows to take revenge on those who attacked his father."
    }
}

# Default for others
default_poster = "https://images.unsplash.com/photo-1485846234645-a62644f84728?q=80&w=1000&auto=format&fit=crop"
default_banner = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?q=80&w=2000&auto=format&fit=crop"

def update_movies():
    res = requests.get(f"{BASE_URL}/api/movies/all?pageSize=100", headers=headers)
    movies = res.json().get("content", [])
    
    for m in movies:
        title = m['title']
        mid = m['id']
        data = movie_metadata.get(title, {
            "poster": default_poster,
            "banner": default_banner,
            "desc": f"Experience the magic of {title} on the big screen. A cinematic masterpiece await."
        })
        
        payload = {
            "posterUrl": data["poster"],
            "bannerUrl": data["banner"],
            "description": data["desc"],
            "leadActor": "Various Stars" if "leadActor" not in m else m["leadActor"]
        }
        
        upd_res = requests.put(f"{BASE_URL}/api/movies/update/{mid}", json=payload, headers=headers)
        if upd_res.status_code == 200:
            print(f"Updated metadata for: {title}")
        else:
            print(f"Failed to update {title}: {upd_res.text}")

if __name__ == "__main__":
    update_movies()

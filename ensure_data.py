import requests
from datetime import datetime, timedelta

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

# Fetch existing movies
movies_res = requests.get(f"{BASE_URL}/api/movies/all?pageSize=100", headers=headers)
if movies_res.status_code != 200:
    print(f"Failed to fetch movies: {movies_res.text}")
    exit(1)
all_movies = movies_res.json().get("content", [])

# Filter now showing movies (release date in the past)
today_dt = datetime.now()
now_showing_ids = []
for m in all_movies:
    rd = m.get("releaseDate")
    if rd:
        rd_dt = datetime.strptime(rd, "%Y-%m-%d")
        if rd_dt < today_dt:
            now_showing_ids.append(m.get("id"))

print(f"Found {len(now_showing_ids)} now-showing movies.")

# Fetch all theaters
theaters_res = requests.get(f"{BASE_URL}/api/theatre/list?pageSize=50", headers=headers)
if theaters_res.status_code != 200:
    print(f"Failed to fetch theaters: {theaters_res.text}")
    exit(1)
all_theaters = theaters_res.json().get("content", [])

print(f"Found {len(all_theaters)} theaters.")

# Ensure screens exist for each theater
for t in all_theaters:
    tid = t.get("id")
    tname = t.get("name")
    
    # Fetch screens for this theater
    screens_res = requests.get(f"{BASE_URL}/api/screens/theatre/{tid}", headers=headers)
    if screens_res.status_code == 200:
        screens = screens_res.json()
        if not screens:
            print(f"Adding screens for theater {tname} (ID: {tid})...")
            for i in range(1, 4):
                url = f"{BASE_URL}/api/screens/create"
                payload = {
                    "name": f"Screen {i}",
                    "theatreId": tid,
                    "screenType": "REGULAR" if i < 3 else "IMAX",
                    "totalSeats": 150,
                    "isActive": True
                }
                requests.post(url, json=payload, headers=headers)
            # Re-fetch screens
            screens = requests.get(f"{BASE_URL}/api/screens/theatre/{tid}", headers=headers).json()
        
        print(f"Theater {tname} has {len(screens)} screens.")
        
        # Add shows for now showing movies
        if now_showing_ids:
            today_str = today_dt.strftime("%Y-%m-%d")
            tomorrow_str = (today_dt + timedelta(days=1)).strftime("%Y-%m-%d")
            
            for s in screens[:2]: # Add shows for 2 screens per theater
                sid = s.get("id")
                # Add 2 shows per screen for today and tomorrow
                for idx, mid in enumerate(now_showing_ids[:2]):
                    # Today
                    show_payload = {
                        "movieId": mid,
                        "screenId": sid,
                        "showDate": today_str,
                        "showTime": "14:00:00" if idx == 0 else "20:00:00",
                        "language": "Hindi",
                        "seatPrices": {"GOLD": 450.0, "SILVER": 250.0, "BRONZE": 150.0}
                    }
                    requests.post(f"{BASE_URL}/api/show/create", json=show_payload, headers=headers)
                    
                    # Tomorrow
                    show_payload["showDate"] = tomorrow_str
                    requests.post(f"{BASE_URL}/api/show/create", json=show_payload, headers=headers)

print("Data Population (Ensuring Screens & Shows) Completed!")

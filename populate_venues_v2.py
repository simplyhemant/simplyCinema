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

# 1. Create Owners
def create_owner(name, email):
    # Signup
    signup_url = f"{BASE_URL}/api/auth/signup/pass"
    payload = {
        "firstName": name,
        "email": email,
        "password": "password123",
        "phone": "9000000000"
    }
    signup_res = requests.post(signup_url, json=payload)
    
    # Get User ID (Fetch all users from admin)
    users_res = requests.get(f"{BASE_URL}/admin/all/users", headers=headers)
    users = users_res.json()
    user_id = next((u['id'] for u in users if u['email'] == email), None)
    
    # Assign Role
    if user_id:
        assign_url = f"{BASE_URL}/admin/assign?userId={user_id}&roleName=THEATRE_OWNER"
        requests.post(assign_url, headers=headers)
        print(f"Created Owner: {name} (ID: {user_id})")
        return user_id
    return None

owner_ids = []
for i in range(1, 6):
    oid = create_owner(f"Owner {i}", f"owner{i}@simplycinema.com")
    if oid: owner_ids.append(oid)

# 2. Fetch Cities
cities_res = requests.get(f"{BASE_URL}/api/cities/all", headers=headers)
city_map = {c['name']: c['id'] for c in cities_res.json()}

# 3. Create Theaters
theaters = [
    ("PVR Director's Cut", "Delhi NCR", "Ambience Mall, Vasant Kunj", "delhi_pvr_dc@example.com"),
    ("PVR Superplex", "Delhi NCR", "Logix City Centre, Noida", "delhi_pvr_sp@example.com"),
    ("Inox Insignia", "Delhi NCR", "Epicuria Mall, Nehru Place", "delhi_inox_in@example.com"),
    ("Cinépolis VIP", "Bangalore", "Orion Mall, Rajajinagar", "blr_cine_vip@example.com"),
    ("PVR ICE", "Bangalore", "Nexus Shantiniketan", "blr_pvr_ice@example.com"),
    ("Inox Megaplex", "Bangalore", "Forum Mall, Kanakapura", "blr_inox_mp@example.com"),
    ("Satyamev Cinema", "Indore", "Vijaynagar, Indore", "ind_satyam@example.com"),
    ("Malhar Megaplex", "Indore", "Malhar Mega Mall", "ind_malhar@example.com"),
    ("Inox Phoenix Citadel", "Indore", "MR 10 Road", "ind_inox_phnx@example.com"),
    ("The Grand Cinema", "Delhi NCR", "Saket District Centre", "delhi_grand@example.com")
]

def add_theater(name, city_name, address, email, owner_id):
    url = f"{BASE_URL}/api/theatre/owner/create"
    payload = {
        "name": name,
        "cityId": city_map[city_name],
        "address": address,
        "phone": "9123456789",
        "email": email,
        "ownerId": owner_id,
        "amenities": ["Parking", "Recliner", "Dolby Atmos", "Food & Beverage"],
        "foodBeverageAvailable": True,
        "openingHour": "09:00:00",
        "closingHour": "23:59:00"
    }
    res = requests.post(url, json=payload, headers=headers)
    if res.status_code == 200:
        print(f"Theater {name} added for Owner {owner_id}.")
        return res.json().get("id")
    else:
        print(f"Failed to add {name}: {res.text}")
        return None

theater_ids = []
for idx, t in enumerate(theaters):
    owner_id = owner_ids[idx % len(owner_ids)]
    tid = add_theater(t[0], t[1], t[2], t[3], owner_id)
    if tid: theater_ids.append(tid)

# 4. Create Screens (4-5 per theater)
for tid in theater_ids:
    num_screens = 4 if tid % 2 == 0 else 5
    for i in range(1, num_screens + 1):
        payload = {
            "name": f"Auditorium {i}",
            "theatreId": tid,
            "screenType": "REGULAR" if i < 3 else ("IMAX" if i == 3 else "DOLBY_ATMOS"),
            "totalSeats": 100 + (i * 20),
            "isActive": True
        }
        res = requests.post(f"{BASE_URL}/api/screens/create", json=payload, headers=headers)
        if res.status_code == 200:
            pass # print(f"Screen {i} added for theater {tid}")

print("Data Population Completed successfully!")

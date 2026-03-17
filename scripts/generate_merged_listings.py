import csv
import json
import random
import time
from pathlib import Path

import requests


REPO_ROOT = Path(__file__).resolve().parent.parent
DATA_DIR = REPO_ROOT / "src" / "main" / "resources" / "commute_data"
DEFAULT_INPUT_CSV = DATA_DIR / "Rental_List.csv"
DEFAULT_OUTPUT_CSV = DATA_DIR / "Rental_List2.csv"

def get_real_address(postal_code, fallback_area):
    """Fetches the real block and street name from OneMap API based on postal code."""
    url = f"https://www.onemap.gov.sg/api/common/elastic/search?searchVal={postal_code}&returnGeom=N&getAddrDetails=Y"
    
    try:
        response = requests.get(url, timeout=5)
        if response.status_code == 200:
            data = response.json()
            if data['found'] > 0:
                result = data['results'][0]
                blk = result.get('BLK_NO', '')
                road = result.get('ROAD_NAME', '')
                
                # Format nicely, e.g., "Blk 150 Cantonment Road"
                if blk and blk != "NIL":
                    return f"Blk {blk} {road.title()}"
                elif road and road != "NIL":
                    return road.title()
    except requests.exceptions.RequestException:
        pass
        
    # Fallback if API fails or postal is invalid
    return f"Blk {random.randint(1, 200)} {fallback_area} Road"

def generate_merged_listings(input_csv=DEFAULT_INPUT_CSV, output_csv=DEFAULT_OUTPUT_CSV):
    adjectives = ["City-fringe", "Quiet stay near", "Budget", "Studio-style", "Central", "Premium", "Breezy", "Cozy", "Spacious"]
    room_types = ["Singleroom", "Condo room", "HDB room", "Common room", "Shared room", "Private room"]
    platforms = ["PropertyGuru", "99.co"]
    notes_pool = [
        "Curated demo listing", "Near central amenities", "Good value option", 
        "Higher rent but newer estate", "Walkable to town center", "Student-friendly budget", 
        "Good city access", "Convenient bus access", "Premium central option", 
        "Simple central listing", "Affordable option", "Quiet neighborhood"
    ]

    try:
        print(f"Reading from {input_csv} and querying OneMap for real addresses...")
        with Path(input_csv).open(mode='r', encoding='utf-8-sig') as infile:
            reader = csv.DictReader(infile)
            
            with Path(output_csv).open(mode='w', newline='', encoding='utf-8') as outfile:
                # Updated headers: Restored flat_id, removed fares
                headers = ["listingId", "title", "monthlyRent", "flat_id", "address", "roomType", "sourcePlatform", "notes", "amenities"]
                writer = csv.DictWriter(outfile, fieldnames=headers)
                writer.writeheader()
                
                for index, row in enumerate(reader, start=1):
                    flat_id = row['Flat_ID']
                    postal = str(row['Postal_Code']).strip()
                    area = row['Area_Name'].split(' / ')[0].strip() 
                    
                    print(f"  Fetching data for Flat {flat_id} (Postal: {postal})...")
                    
                    # 1. Base Information
                    listing_id = f"L{index:03d}"
                    room_type = random.choice(room_types)
                    title = f"{random.choice(adjectives)} {room_type.lower()} in {area}"
                    platform = random.choice(platforms)
                    note = random.choice(notes_pool)
                    
                    # Get real address via API
                    address = get_real_address(postal, area)
                    time.sleep(0.2) # Polite delay for the API
                    
                    # 2. Logical Rent Generation
                    is_condo = "Condo" in room_type or "Private" in room_type
                    if is_condo:
                        rent = random.randint(18, 30) * 100 + random.choice([0, 50])
                    else:
                        rent = random.randint(10, 17) * 100 + random.choice([0, 20, 50, 80])
                        
                    # 3. Amenities Logic (Nested Dictionary)
                    amenities = {
                        "hasAircon": random.choice([True, True, True, False]), # 75% chance
                        "hasHeater": random.choice([True, False]),
                        "hasSwimmingPool": is_condo, # Condos have pools, HDBs don't
                        "hasGym": is_condo
                    }
                    
                    # Write row, converting the amenities dict to a JSON string
                    writer.writerow({
                        "listingId": listing_id,
                        "title": title,
                        "monthlyRent": rent,
                        "flat_id": flat_id,
                        "address": address,
                        "roomType": room_type,
                        "sourcePlatform": platform,
                        "notes": note,
                        "amenities": json.dumps(amenities)
                    })
                    
        print(f"\nSuccess! Generated '{output_csv}' with real addresses and nested JSON amenities.")
        
    except FileNotFoundError:
        print(f"[!] Error: Could not find '{input_csv}'. Please verify the path or pass --input.")

if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser(
        description="Generate merged rental listings with enriched addresses and amenities."
    )
    parser.add_argument(
        "--input",
        default=str(DEFAULT_INPUT_CSV),
        help="Input rental CSV path (default: repo-relative Rental_List.csv).",
    )
    parser.add_argument(
        "--output",
        default=str(DEFAULT_OUTPUT_CSV),
        help="Output CSV path (default: repo-relative Rental_List2.csv).",
    )
    args = parser.parse_args()

    generate_merged_listings(args.input, args.output)




    
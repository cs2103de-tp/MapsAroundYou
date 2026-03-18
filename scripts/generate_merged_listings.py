import csv
import random
import time
from pathlib import Path

import requests


REPO_ROOT = Path(__file__).resolve().parent.parent
DATA_DIR = REPO_ROOT / "src" / "main" / "resources" / "commute_data"
DEFAULT_INPUT_CSV = DATA_DIR / "Rental_List.csv"
DEFAULT_OUTPUT_CSV = DATA_DIR / "listings.csv"

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
    """
    Generates listings.csv from Rental_List.csv with enriched data.
    Maps Flat_ID to originNodeId and generates app-facing listing fields.
    """
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
                # Headers matching Java app's RentalListing model
                headers = ["listingId", "title", "monthlyRent", "hasAircon", "originNodeId", "address", "roomType", "sourcePlatform", "notes"]
                writer = csv.DictWriter(outfile, fieldnames=headers)
                writer.writeheader()
                
                for index, row in enumerate(reader, start=1):
                    flat_id = row['Flat_ID']
                    postal = str(row['Postal_Code']).strip()
                    area = row['Area_Name'].split(' / ')[0].strip() 
                    
                    print(f"  Generating listing for Flat {flat_id} (Postal: {postal})...")
                    
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
                    
                    # 3. hasAircon as top-level boolean (75% chance)
                    has_aircon = random.choice([True, True, True, False])
                    
                    # Write row matching the Java RentalListing schema
                    writer.writerow({
                        "listingId": listing_id,
                        "title": title,
                        "monthlyRent": rent,
                        "hasAircon": "true" if has_aircon else "false",
                        "originNodeId": flat_id,
                        "address": address,
                        "roomType": room_type,
                        "sourcePlatform": platform,
                        "notes": note
                    })
                    
        print(f"\nSuccess! Generated '{output_csv}' with app-facing listing fields.")
        
    except FileNotFoundError:
        print(f"[!] Error: Could not find '{input_csv}'. Please verify the path or pass --input.")

if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser(
        description="Generate listings.csv from Rental_List.csv with enriched addresses."
    )
    parser.add_argument(
        "--input",
        default=str(DEFAULT_INPUT_CSV),
        help="Input rental CSV path (default: repo-relative Rental_List.csv).",
    )
    parser.add_argument(
        "--output",
        default=str(DEFAULT_OUTPUT_CSV),
        help="Output CSV path (default: repo-relative listings.csv).",
    )
    args = parser.parse_args()

    generate_merged_listings(args.input, args.output)




    
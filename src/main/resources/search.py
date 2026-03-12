from deepface import DeepFace
import sys
import json

def find_similar_images(query_image_path, images_folder_path):
    try:
        results = []
        result = DeepFace.find(query_image_path, db_path=images_folder_path, model_name='VGG-Face')
        
        # Debugging: print the type and content of result
        print(f"Debug: Type of result - {type(result)}")
        print(f"Debug: Content of result - {result}")

        for df_result in result:
            # Debugging: print the type and content of each df_result
            print(f"Debug: Type of df_result - {type(df_result)}")
            print(f"Debug: Content of df_result - {df_result}")

            # Extract relevant information from the result
            for index, row in df_result.iterrows():
                # Collect image path and score
                image_url = row['identity']
                score = row['distance']  # Or use the column name that contains the similarity score
                
                # Debugging: print each comparison
                print(f"Debug: Comparing {query_image_path} with {image_url}, score: {score}")
                
                # Append all results to the list
                results.append({
                    'image_url': image_url,
                    'score': score
                })
        
        # Remove duplicates and convert to JSON
        results = list({(d['image_url'], d['score']): d for d in results}.values())
        return json.dumps(results, indent=4)
    
    except Exception as e:
        return json.dumps({"error": str(e)})

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print(json.dumps({"error": "Invalid number of arguments"}))
        sys.exit(1)
    query_image_path = sys.argv[1]
    images_folder_path = sys.argv[2]
    output = find_similar_images(query_image_path, images_folder_path)
    print(output)
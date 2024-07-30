import boto3
import psycopg2
from psycopg2 import pool

# AWS credentials and Aurora DB details
aws_region = 'us-west-2'  # Example region
db_cluster_identifier = 'my-aurora-cluster'
db_name = 'mydatabase'
db_user = 'myusername'
db_password = 'mypassword'

# Initialize a session using Boto3 to retrieve the Aurora endpoint
session = boto3.Session(
    aws_access_key_id='YOUR_ACCESS_KEY',
    aws_secret_access_key='YOUR_SECRET_KEY',
    region_name=aws_region
)

# Use RDS client to get the Aurora endpoint
rds_client = session.client('rds')

# Describe DB cluster to get the writer endpoint (for read/write)
response = rds_client.describe_db_clusters(
    DBClusterIdentifier=db_cluster_identifier
)

# Extract the endpoint
db_endpoint = response['DBClusters'][0]['Endpoint']

# Define the connection string
connection_string = f"dbname='{db_name}' user='{db_user}' password='{db_password}' host='{db_endpoint}'"

# Initialize connection pool
try:
    connection_pool = psycopg2.pool.SimpleConnectionPool(
        1,  # Minimum number of connections
        10,  # Maximum number of connections
        connection_string
    )

    if connection_pool:
        print("Connection pool created successfully")

except Exception as e:
    print(f"Error while connecting to PostgreSQL: {e}")

# Function to get a connection from the pool
def get_connection():
    try:
        # Get a connection from the pool
        connection = connection_pool.getconn()
        if connection:
            print("Successfully received a connection from the connection pool")
        return connection
    except Exception as e:
        print(f"Error getting connection from pool: {e}")

# Function to release a connection back to the pool
def release_connection(connection):
    try:
        # Release the connection back to the pool
        connection_pool.putconn(connection)
        print("Connection returned to the pool")
    except Exception as e:
        print(f"Error releasing connection back to pool: {e}")

# Example function to execute a query
def execute_query(query):
    connection = None
    try:
        # Get a connection from the pool
        connection = get_connection()
        cursor = connection.cursor()

        # Execute the query
        cursor.execute(query)

        # Fetch the results
        results = cursor.fetchall()
        print("Query executed successfully, results:", results)

    except Exception as e:
        print(f"Error executing query: {e}")

    finally:
        if connection:
            # Close the cursor and release the connection
            cursor.close()
            release_connection(connection)

# Example usage
if __name__ == "__main__":
    test_query = "SELECT * FROM your_table LIMIT 5;"
    execute_query(test_query)

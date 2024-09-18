if [ "$1" == "" ]; then
    echo "The first argument should be the API's base url." 1>&2
    exit 1
fi

if [ "$2" == "" ]; then
    echo "The second argument should be the number of calls." 1>&2
    exit 1
fi

baseurl=$1
loops=$2

x=1
while [ $x -le $loops ]
do

curl --location --request GET "$baseurl/api/v1/catalog" -H "Content-Type: application/json"

curl --location --request GET "$baseurl/api/v1/catalog/100" -H "Content-Type: application/json"

curl --location --request GET "$baseurl/api/v1/payments" -H "Content-Type: application/json"

curl --location --request GET "$baseurl/api/v1/payments/" -H "Content-Type: application/json"

curl --location --request POST "$baseurl/api/v1/order" -H "Content-Type: application/json" \
--data-raw '{
    "catalogItems": [
        {
            "id": 1,
            "name": "Turtle Beach Recon 200 Headset",
            "imageSource": null,
            "description": "Powerful amplified audio: Immerse yourself in your games with rechargeable, battery powered amplified sound from your Xbox and PlayStation",
            "amount": 49.95,
            "inStock": true
        }
    ],
    "billingAddress": {
        "firstName": "Brian",
        "lastName": "Jimerson",
        "email": "bjimerson@example.com",
        "address": "1234 Main St",
        "address2": null,
        "city": "Powell",
        "state": "OH",
        "zipCode": "43065"
    },
    "payment": {
        "cardNumber": "4242424242424242",
        "cvc": "123",
        "expirationMonth": 1,
        "expirationYear": 2025,
        "amount": 49.95,
        "currency": "usd",
        "description": "Test API order payment"
    },
    "orderTotal": 49.95
}'
x=$(( $x +1 ))
done

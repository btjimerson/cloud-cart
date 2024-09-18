#!/usr/bin/python

import random
from locust import HttpUser, TaskSet, between

products = [
    '100',
    '200',
    '300',
    '400',
    '500',
    '600',
    '700',
    '800',
    '900'
]

def index(l):
    l.client.get("/")

def browseCatalog(l):
    l.client.get("/item/" + random.choice(products))

def viewCart(l):
    l.client.get("/checkout")

def addToCart(l):
    product = random.choice(products)
    l.client.get("/cart/" + product)

def checkout(l):
    addToCart(l)
    l.client.post("/order", {
        "catalogItems": [
            {
                "id": 1,
                "name": "Turtle Beach Recon 200 Headset",
                "imageSource": None,
                "description": "Powerful amplified audio: Immerse yourself in your games with rechargeable, battery powered amplified sound from your Xbox and PlayStation",
                "amount": 49.95,
                "inStock": True
            }
        ],
        "billingAddress": {
            "firstName": "Sally",
            "lastName": "Struthers",
            "email": "sstruthers@example.com",
            "address": "1234 Main St",
            "address2": None,
            "city": "Des Moines",
            "state": "IA",
            "zipCode": "50047"
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
    })

class UserBehavior(TaskSet):

    def on_start(self):
        index(self)

    tasks = {index: 1,
        browseCatalog: 10,
        addToCart: 2,
        viewCart: 3,
        addToCart: 3,
        checkout: 2}

class WebsiteUser(HttpUser):
    tasks = [UserBehavior]
    wait_time = between(1, 10)

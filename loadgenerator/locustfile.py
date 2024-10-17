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
        "billingAddress.firstName": "Sally",
        "billingAddress.lastName": "Struthers",
        "billingAddress.email": "sstruthers@example.com",
        "billingAddress.address": "1234 Main St.",
        "billingAddress.city": "Des Moines",
        "billingAddress.state": "IA",
        "billingAddress.zipCode": "50047",
        "payment.cardNumber": "4242424242424242",
        "payment.expirationMonth": "01",
        "payment.expirationYear": "29",
        "payment.cvc": "123"
    }
)

def paymentHistory(l):
    l.client.get("/payments")

class UserBehavior(TaskSet):

    def on_start(self):
        index(self)

    tasks = {index: 1,
        browseCatalog: 10,
        addToCart: 2,
        viewCart: 3,
        addToCart: 3,
        checkout: 2,
        paymentHistory: 2}

class WebsiteUser(HttpUser):
    tasks = [UserBehavior]
    wait_time = between(1, 10)

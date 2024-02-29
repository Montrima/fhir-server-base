db.createUser({
    user: 'fhir',
    pwd: 'fhir123',
    roles: [
        {
            role: 'readWrite',
            db: 'fhir_demo'
        }
    ]
})
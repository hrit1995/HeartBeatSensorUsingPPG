from sqlalchemy import create_engine, Integer, Boolean, Column
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

engine = create_engine("mysql+pymysql://root:lzp123456@localhost:3306/iot_proj", echo=True)
base = declarative_base()
Session = sessionmaker(bind=engine)
session = Session()


class heartRate(base):
    __tablename__ = "heartrate"
    time = Column(Integer, primary_key=True, autoincrement=True)
    rdata = Column(Integer)
    avgbpm = Column(Integer)
    is_anxiety = Column(Boolean)

if __name__ == "__main__":
    base.metadata.create_all(bind=engine)

    session.add(heartRate(time=1, rdata=30, avgbpm=78, is_anxiety=False))
    session.commit()

    for data in session.query(heartRate).all():
        print("Data: {} | {} | {} | {}".format(data.time, data.rdata, data.avgbpm, data.is_anxiety))

